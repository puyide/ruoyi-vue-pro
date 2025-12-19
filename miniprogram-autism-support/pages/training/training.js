// pages/training/training.js
const app = getApp();
const api = require('../../utils/api');

Page({
  data: {
    // 日期信息
    today: '',
    
    // 儿童选择
    children: [],
    currentChild: null,
    showChildPicker: false,
    
    // 今日任务
    todaySessions: [],
    currentSession: null,
    
    // 本周进度
    weekProgress: 0,
    completedDays: 0,
    totalMinutes: 0,
    streak: 0,
    weekDays: [],
    
    // 训练领域
    domains: [],
    
    // 推荐任务
    recommendTemplates: [],
    
    // 加载状态
    loading: true,
    
    // 反馈弹窗
    showFeedbackModal: false,
    feedback: '',
    completedSession: null
  },

  onLoad() {
    this.setToday();
  },

  onShow() {
    this.loadData();
    
    // 检查是否从训练完成页返回
    const completedSessionId = wx.getStorageSync('trainingCompleted');
    if (completedSessionId) {
      wx.removeStorageSync('trainingCompleted');
      this.setData({ 
        showFeedbackModal: true,
        completedSession: { id: completedSessionId }
      });
    }
  },

  // 设置今日日期
  setToday() {
    const now = new Date();
    const month = now.getMonth() + 1;
    const date = now.getDate();
    const weekDay = ['日', '一', '二', '三', '四', '五', '六'][now.getDay()];
    this.setData({
      today: `${month}月${date}日 周${weekDay}`
    });
  },

  // 加载所有数据
  async loadData() {
    this.setData({ loading: true });
    
    try {
      // 先加载儿童列表
      await this.loadChildren();
      
      // 并行加载其他数据
      await Promise.all([
        this.loadDomains(),
        this.loadTodaySessions(),
        this.loadWeeklyProgress(),
        this.loadRecommendTemplates()
      ]);
    } catch (err) {
      console.error('加载数据失败:', err);
      // 使用模拟数据
      this.loadMockData();
    } finally {
      this.setData({ loading: false });
    }
  },

  // 加载儿童列表
  async loadChildren() {
    try {
      const children = await api.getChildrenList();
      if (children && children.length > 0) {
        const currentChild = children[0];
        this.setData({ 
          children,
          currentChild 
        });
      } else {
        // 没有儿童信息时使用默认数据
        this.setData({
          children: [],
          currentChild: { id: 0, nickname: '我的宝贝', age: 4 }
        });
      }
    } catch (err) {
      console.error('加载儿童列表失败:', err);
      this.setData({
        children: [],
        currentChild: { id: 0, nickname: '我的宝贝', age: 4 }
      });
    }
  },

  // 加载训练领域
  async loadDomains() {
    try {
      const domains = await api.getTrainingDomains();
      this.setData({ domains });
    } catch (err) {
      // 使用本地定义的领域数据
      const domains = Object.values(api.TrainingDomains).map(d => ({
        code: d.code,
        name: d.name,
        icon: d.icon,
        color: d.color
      }));
      this.setData({ domains });
    }
  },

  // 加载今日训练会话
  async loadTodaySessions() {
    if (!this.data.currentChild?.id) {
      this.setData({
        todaySessions: [],
        currentSession: this.getMockTodayTask()
      });
      return;
    }

    try {
      const sessions = await api.getTodayTrainingSessions(this.data.currentChild.id);
      const currentSession = sessions && sessions.length > 0 
        ? sessions.find(s => s.status === 0) || sessions[0]  // 优先显示未完成的
        : null;
      
      this.setData({ 
        todaySessions: sessions || [],
        currentSession: currentSession || this.getMockTodayTask()
      });
    } catch (err) {
      console.error('加载今日会话失败:', err);
      this.setData({
        todaySessions: [],
        currentSession: this.getMockTodayTask()
      });
    }
  },

  // 加载本周进度
  async loadWeeklyProgress() {
    if (!this.data.currentChild?.id) {
      this.generateMockWeekDays();
      return;
    }

    try {
      const [statistics, weeklyLogs] = await Promise.all([
        api.getTrainingStatistics(this.data.currentChild.id),
        api.getWeeklyTrainingLogs(this.data.currentChild.id)
      ]);

      // 计算本周进度
      const weekProgress = statistics?.weeklyCompletionRate || 0;
      const completedDays = statistics?.weeklyCompletedDays || 0;
      const totalMinutes = statistics?.weeklyTotalMinutes || 0;
      const streak = statistics?.streak || 0;

      // 生成最近7天的打卡状态
      const weekDays = this.generateWeekDaysFromLogs(weeklyLogs || []);

      this.setData({
        weekProgress,
        completedDays,
        totalMinutes,
        streak,
        weekDays
      });
    } catch (err) {
      console.error('加载周进度失败:', err);
      this.generateMockWeekDays();
    }
  },

  // 根据日志生成最近7天打卡数据
  generateWeekDaysFromLogs(logs) {
    const days = ['日', '一', '二', '三', '四', '五', '六'];
    const weekDays = [];
    const now = new Date();
    
    // 创建日期到日志的映射
    const logMap = {};
    logs.forEach(log => {
      const dateStr = log.createTime?.split('T')[0] || log.createTime?.split(' ')[0];
      if (dateStr) {
        logMap[dateStr] = log;
      }
    });

    for (let i = 6; i >= 0; i--) {
      const date = new Date(now);
      date.setDate(date.getDate() - i);
      
      const dateStr = date.toISOString().split('T')[0];
      const log = logMap[dateStr];
      
      let status = 'pending';
      if (i === 0) {
        // 今天
        status = log?.completed ? 'completed' : 'pending';
      } else {
        status = log?.completed ? 'completed' : 'missed';
      }

      weekDays.push({
        name: `周${days[date.getDay()]}`,
        date: date.getDate(),
        status,
        isToday: i === 0
      });
    }

    return weekDays;
  },

  // 生成模拟的周数据
  generateMockWeekDays() {
    const days = ['日', '一', '二', '三', '四', '五', '六'];
    const weekDays = [];
    const now = new Date();
    
    for (let i = 6; i >= 0; i--) {
      const date = new Date(now);
      date.setDate(date.getDate() - i);
      
      let status = 'pending';
      if (i === 0) {
        status = 'pending'; // 今天
      } else if (i <= 3 && Math.random() > 0.3) {
        status = 'completed';
      } else if (i <= 3) {
        status = 'missed';
      }

      weekDays.push({
        name: `周${days[date.getDay()]}`,
        date: date.getDate(),
        status,
        isToday: i === 0
      });
    }

    // 计算模拟的统计数据
    const completedDays = weekDays.filter(d => d.status === 'completed').length;
    const weekProgress = Math.round((completedDays / 7) * 100);

    this.setData({ 
      weekDays,
      completedDays,
      weekProgress,
      totalMinutes: completedDays * 15,
      streak: Math.min(completedDays, 3)
    });
  },

  // 加载推荐模板
  async loadRecommendTemplates() {
    try {
      // 从多个领域获取推荐
      const domains = [1, 2, 3]; // 社交、语言、情绪
      const templates = [];
      
      for (const domain of domains) {
        try {
          const domainTemplates = await api.getTrainingTemplates(domain);
          if (domainTemplates && domainTemplates.length > 0) {
            templates.push(domainTemplates[0]); // 每个领域取第一个
          }
        } catch (e) {
          // 忽略单个领域的错误
        }
      }

      if (templates.length > 0) {
        this.setData({ recommendTemplates: templates });
      } else {
        this.setData({ recommendTemplates: this.getMockRecommendTasks() });
      }
    } catch (err) {
      console.error('加载推荐模板失败:', err);
      this.setData({ recommendTemplates: this.getMockRecommendTasks() });
    }
  },

  // 获取模拟的今日任务
  getMockTodayTask() {
    return {
      id: '1',
      templateName: '眼神跟随训练',
      domain: 1,
      domainName: '社交互动',
      domainIcon: '👀',
      duration: '5-10分钟',
      briefSteps: [
        '展示孩子喜欢的玩具',
        '等待孩子看向你',
        '给予即时强化奖励'
      ],
      difficultyLevel: 1,
      status: 0
    };
  },

  // 获取模拟的推荐任务
  getMockRecommendTasks() {
    return [
      {
        id: '2',
        name: '呼名反应训练',
        icon: '👂',
        domain: 2,
        domainName: '语言沟通',
        ageRange: '3-6岁'
      },
      {
        id: '3',
        name: '情绪识别卡片',
        icon: '😊',
        domain: 3,
        domainName: '情绪管理',
        ageRange: '3-6岁'
      },
      {
        id: '4',
        name: '触觉脱敏游戏',
        icon: '✋',
        domain: 5,
        domainName: '感觉统合',
        ageRange: '0-6岁'
      }
    ];
  },

  // 加载模拟数据（开发用）
  loadMockData() {
    this.setData({
      currentChild: { id: 0, nickname: '我的宝贝', age: 4 },
      currentSession: this.getMockTodayTask(),
      recommendTemplates: this.getMockRecommendTasks(),
      domains: Object.values(api.TrainingDomains).map(d => ({
        code: d.code,
        name: d.name,
        icon: d.icon,
        color: d.color
      }))
    });
    this.generateMockWeekDays();
  },

  // ==================== 事件处理 ====================

  // 切换儿童
  showChildSelector() {
    if (this.data.children.length <= 1) {
      wx.showToast({ title: '暂无其他宝贝', icon: 'none' });
      return;
    }
    this.setData({ showChildPicker: true });
  },

  selectChild(e) {
    const index = e.currentTarget.dataset.index;
    const child = this.data.children[index];
    this.setData({
      currentChild: child,
      showChildPicker: false
    });
    // 重新加载数据
    this.loadTodaySessions();
    this.loadWeeklyProgress();
  },

  closeChildPicker() {
    this.setData({ showChildPicker: false });
  },

  // 开始训练
  startTraining(e) {
    const session = this.data.currentSession;
    if (!session) {
      wx.showToast({ title: '暂无训练任务', icon: 'none' });
      return;
    }

    if (session.id && session.id !== '1') {
      // 真实会话，直接进入执行页面
      wx.navigateTo({
        url: `/pages/training-execute/training-execute?sessionId=${session.id}`
      });
    } else {
      // 模拟会话，先进入详情页
      wx.navigateTo({
        url: `/pages/training-detail/training-detail?id=${session.id}`
      });
    }
  },

  // 查看任务详情（推荐任务）
  viewTaskDetail(e) {
    const { id, type } = e.currentTarget.dataset;
    if (type === 'template') {
      // 模板详情，需要先选择难度创建会话
      wx.navigateTo({
        url: `/pages/training-detail/training-detail?templateId=${id}`
      });
    } else {
      wx.navigateTo({
        url: `/pages/training-detail/training-detail?id=${id}`
      });
    }
  },

  // 查看训练领域
  viewDomain(e) {
    const { code, name } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/training-template/training-template?domain=${code}&name=${encodeURIComponent(name)}`
    });
  },

  // 查看全部任务/领域
  viewAllDomains() {
    wx.navigateTo({
      url: '/pages/training-domain/training-domain'
    });
  },

  // 添加新任务
  addNewTask() {
    wx.navigateTo({
      url: '/pages/training-domain/training-domain'
    });
  },

  // ==================== 反馈弹窗 ====================

  selectFeedback(e) {
    this.setData({
      feedback: e.currentTarget.dataset.value
    });
  },

  // 提交反馈并关闭弹窗
  async submitFeedback() {
    const { feedback, completedSession } = this.data;
    
    // 如果有真实会话ID，提交到服务器
    if (completedSession?.id && feedback) {
      try {
        // 将反馈映射为心情枚举值
        const moodMap = {
          'good': 1,    // GOOD
          'normal': 2,  // NORMAL
          'hard': 3     // BAD
        };
        
        await api.createTrainingLog({
          sessionId: completedSession.id,
          completed: true,
          childMood: moodMap[feedback] || 2,
          parentComment: ''
        });
      } catch (err) {
        console.error('提交反馈失败:', err);
      }
    }

    this.closeFeedbackModal();
  },

  viewRelatedPosts() {
    this.setData({ showFeedbackModal: false });
    wx.switchTab({
      url: '/pages/community/community'
    });
  },

  closeFeedbackModal() {
    this.setData({
      showFeedbackModal: false,
      feedback: '',
      completedSession: null
    });
    
    // 刷新数据
    this.loadTodaySessions();
    this.loadWeeklyProgress();
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadData().finally(() => {
      wx.stopPullDownRefresh();
    });
  }
});
