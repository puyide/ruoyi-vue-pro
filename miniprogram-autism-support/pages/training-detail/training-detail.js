// pages/training-detail/training-detail.js
const app = getApp();
const api = require('../../utils/api');

Page({
  data: {
    // 参数
    sessionId: null,
    templateId: null,
    level: 1,
    
    // 会话数据
    session: null,
    
    // AI生成的训练内容
    trainingContent: null,
    
    // 加载状态
    loading: true,
    generating: false,
    
    // 难度等级相关
    difficultyLevel: 1,
    levelLabels: { 1: '初级', 2: '中级', 3: '高级' },
    
    // 儿童信息
    currentChild: null
  },

  onLoad(options) {
    this.setData({
      sessionId: options.sessionId || null,
      templateId: options.templateId || options.id || null,
      level: parseInt(options.level) || 1
    });
    
    this.loadData();
  },

  async loadData() {
    this.setData({ loading: true });
    
    try {
      // 加载儿童信息
      await this.loadChild();
      
      if (this.data.sessionId) {
        // 有会话ID，直接加载会话详情
        await this.loadSession();
      } else if (this.data.templateId) {
        // 有模板ID，加载模板并生成训练内容
        await this.loadTemplateAndGenerate();
      } else {
        // 使用模拟数据
        this.loadMockData();
      }
    } catch (err) {
      console.error('加载失败:', err);
      this.loadMockData();
    } finally {
      this.setData({ loading: false });
    }
  },

  async loadChild() {
    try {
      const children = await api.getChildrenList();
      if (children && children.length > 0) {
        this.setData({ currentChild: children[0] });
      } else {
        this.setData({
          currentChild: { id: 0, nickname: '我的宝贝', age: 4 }
        });
      }
    } catch (err) {
      this.setData({
        currentChild: { id: 0, nickname: '我的宝贝', age: 4 }
      });
    }
  },

  // 加载已有会话
  async loadSession() {
    try {
      const session = await api.getTrainingSessionDetail(this.data.sessionId);
      
      // 解析AI输出内容
      let trainingContent = null;
      if (session.aiOutputJson) {
        try {
          trainingContent = JSON.parse(session.aiOutputJson);
        } catch (e) {
          console.error('解析AI输出失败:', e);
        }
      }
      
      this.setData({
        session,
        trainingContent: trainingContent || this.getMockTrainingContent(session),
        difficultyLevel: session.difficultyLevel || 1
      });
    } catch (err) {
      console.error('加载会话失败:', err);
      throw err;
    }
  },

  // 加载模板并生成训练内容
  async loadTemplateAndGenerate() {
    try {
      // 先获取模板详情
      const template = await api.getTrainingTemplateDetail(this.data.templateId);
      
      this.setData({
        session: {
          templateName: template.name,
          domain: template.domain,
          briefGoal: template.briefGoal
        },
        generating: true
      });

      // 创建会话（触发AI生成）
      const session = await api.createTrainingSession({
        childId: this.data.currentChild?.id || 0,
        templateId: this.data.templateId,
        difficultyLevel: this.data.level
      });

      // 解析AI输出
      let trainingContent = null;
      if (session.aiOutputJson) {
        try {
          trainingContent = JSON.parse(session.aiOutputJson);
        } catch (e) {
          console.error('解析AI输出失败:', e);
        }
      }

      this.setData({
        sessionId: session.id,
        session,
        trainingContent: trainingContent || this.getMockTrainingContent(session),
        difficultyLevel: session.difficultyLevel || this.data.level,
        generating: false
      });
    } catch (err) {
      console.error('生成训练内容失败:', err);
      this.setData({ generating: false });
      // 使用模拟数据
      this.loadMockData();
    }
  },

  // 加载模拟数据
  loadMockData() {
    const mockSession = {
      id: '1',
      templateName: '眼神跟随训练',
      domain: 1,
      domainName: '社交互动',
      difficultyLevel: this.data.level || 1,
      status: 0
    };

    this.setData({
      session: mockSession,
      trainingContent: this.getMockTrainingContent(mockSession),
      difficultyLevel: mockSession.difficultyLevel
    });
  },

  // 获取模拟训练内容
  getMockTrainingContent(session) {
    return {
      title: session?.templateName || '眼神跟随训练',
      icon: '👀',
      duration: '5-10分钟',
      goal: '通过玩具引导，帮助孩子建立眼神接触的习惯，提升共同注意力',
      materials: ['孩子喜欢的玩具', '舒适的环境', '小零食作为奖励'],
      steps: [
        {
          title: '准备阶段',
          description: '选择孩子感兴趣的玩具（如小汽车、毛绒玩具），确保环境安静，减少干扰。让孩子坐在你对面，保持舒适的距离（约1米）。',
          tips: '最好选择孩子当下最喜欢的玩具，增加配合度。'
        },
        {
          title: '吸引注意',
          description: '把玩具举到你的眼睛旁边，轻声叫孩子的名字。如果孩子没有反应，可以轻轻摇动玩具或发出声音吸引注意。',
          tips: '不要强迫孩子看你，保持轻松愉快的氛围。'
        },
        {
          title: '等待反应',
          description: '当孩子看向玩具时，保持不动，等待3-5秒。如果孩子的目光从玩具移到你的眼睛，立即给予积极回应。',
          tips: '这一步可能需要多次尝试，耐心很重要。'
        },
        {
          title: '即时强化',
          description: '当孩子看向你的眼睛时，立即微笑、表扬（"看得真好！"），并把玩具给孩子玩一会儿作为奖励。',
          tips: '强化要及时，在孩子做出正确反应后1-2秒内给予。'
        },
        {
          title: '重复练习',
          description: '等孩子玩一会儿后，轻轻拿回玩具，重复上述步骤。每次训练做3-5轮即可，不要让孩子感到疲倦或厌烦。',
          tips: '观察孩子状态，如果出现抵触情绪及时停止。'
        }
      ],
      notes: [
        '训练时保持轻松愉快的氛围，不要给孩子压力',
        '每天可以训练1-2次，每次5-10分钟',
        '记录孩子的进步，即使是很小的进步也值得庆祝',
        '如果孩子连续几天抵触，可以暂停几天再尝试',
        '可以在日常生活中自然地进行练习'
      ],
      successCriteria: '孩子能主动看向家长眼睛3次以上'
    };
  },

  // 返回
  goBack() {
    wx.navigateBack();
  },

  // 换一个任务
  changeTask() {
    wx.showModal({
      title: '换一个任务',
      content: '是否要换一个训练任务？当前任务将不会保存。',
      success: (res) => {
        if (res.confirm) {
          wx.navigateTo({
            url: '/pages/training-domain/training-domain'
          });
        }
      }
    });
  },

  // 开始训练（进入执行页面）
  startTraining() {
    const { sessionId, trainingContent } = this.data;
    
    if (sessionId) {
      wx.navigateTo({
        url: `/pages/training-execute/training-execute?sessionId=${sessionId}`
      });
    } else {
      // 模拟模式
      wx.navigateTo({
        url: `/pages/training-execute/training-execute?mock=1&title=${encodeURIComponent(trainingContent?.title || '训练')}`
      });
    }
  },

  // 完成任务（直接完成，不进入执行页）
  completeTask() {
    wx.showModal({
      title: '确认完成',
      content: '确定已经完成今天的训练吗？',
      success: (res) => {
        if (res.confirm) {
          if (this.data.sessionId) {
            // 跳转到反馈页面
            wx.navigateTo({
              url: `/pages/training-feedback/training-feedback?sessionId=${this.data.sessionId}`
            });
          } else {
            // 模拟模式，直接返回
            wx.setStorageSync('trainingCompleted', 'mock');
            wx.navigateBack();
          }
        }
      }
    });
  },

  // 调整难度
  adjustLevel(e) {
    const level = parseInt(e.currentTarget.dataset.level);
    if (level === this.data.difficultyLevel) return;
    
    wx.showModal({
      title: '调整难度',
      content: `确定要将难度调整为 ${this.data.levelLabels[level]} 吗？将重新生成训练内容。`,
      success: async (res) => {
        if (res.confirm) {
          this.setData({ 
            level,
            difficultyLevel: level,
            generating: true 
          });
          
          try {
            // 重新创建会话
            const session = await api.createTrainingSession({
              childId: this.data.currentChild?.id || 0,
              templateId: this.data.templateId || this.data.session?.templateId,
              difficultyLevel: level
            });

            let trainingContent = null;
            if (session.aiOutputJson) {
              try {
                trainingContent = JSON.parse(session.aiOutputJson);
              } catch (e) {}
            }

            this.setData({
              sessionId: session.id,
              session,
              trainingContent: trainingContent || this.getMockTrainingContent(session),
              generating: false
            });
          } catch (err) {
            console.error('调整难度失败:', err);
            this.setData({ generating: false });
            wx.showToast({ title: '调整失败', icon: 'none' });
          }
        }
      }
    });
  }
});
