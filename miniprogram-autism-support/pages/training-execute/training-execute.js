// pages/training-execute/training-execute.js
const api = require('../../utils/api');

Page({
  data: {
    // 参数
    sessionId: null,
    mock: false,
    title: '训练中',
    
    // 会话数据
    session: null,
    trainingContent: null,
    
    // 当前步骤
    currentStepIndex: 0,
    totalSteps: 0,
    
    // 计时器
    timerRunning: false,
    timerPaused: false,
    elapsedSeconds: 0,
    displayTime: '00:00',
    
    // 成功计数
    successCount: 0,
    totalAttempts: 0,
    
    // 状态
    loading: true,
    showCompleteModal: false
  },

  timerInterval: null,

  onLoad(options) {
    this.setData({
      sessionId: options.sessionId || null,
      mock: options.mock === '1',
      title: decodeURIComponent(options.title || '训练中')
    });
    
    this.loadData();
  },

  onUnload() {
    // 清理计时器
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  },

  async loadData() {
    this.setData({ loading: true });
    
    try {
      if (this.data.sessionId) {
        await this.loadSession();
      } else {
        this.loadMockData();
      }
    } catch (err) {
      console.error('加载失败:', err);
      this.loadMockData();
    } finally {
      this.setData({ loading: false });
    }
  },

  async loadSession() {
    try {
      const session = await api.getTrainingSessionDetail(this.data.sessionId);
      
      let trainingContent = null;
      if (session.aiOutputJson) {
        try {
          trainingContent = JSON.parse(session.aiOutputJson);
        } catch (e) {}
      }
      
      trainingContent = trainingContent || this.getMockContent();
      
      this.setData({
        session,
        trainingContent,
        title: trainingContent.title || session.templateName || '训练中',
        totalSteps: trainingContent.steps?.length || 0
      });
    } catch (err) {
      console.error('加载会话失败:', err);
      throw err;
    }
  },

  loadMockData() {
    const content = this.getMockContent();
    this.setData({
      trainingContent: content,
      totalSteps: content.steps?.length || 0
    });
  },

  getMockContent() {
    return {
      title: this.data.title || '眼神跟随训练',
      icon: '👀',
      duration: '5-10分钟',
      steps: [
        {
          title: '准备阶段',
          description: '选择孩子感兴趣的玩具，确保环境安静。让孩子坐在你对面，保持舒适的距离。',
          tips: '选择孩子当下最喜欢的玩具，增加配合度。'
        },
        {
          title: '吸引注意',
          description: '把玩具举到你的眼睛旁边，轻声叫孩子的名字。',
          tips: '不要强迫孩子看你，保持轻松愉快的氛围。'
        },
        {
          title: '等待反应',
          description: '当孩子看向玩具时，保持不动，等待3-5秒。',
          tips: '这一步可能需要多次尝试，耐心很重要。'
        },
        {
          title: '即时强化',
          description: '当孩子看向你的眼睛时，立即微笑、表扬，并把玩具给孩子。',
          tips: '强化要及时，在1-2秒内给予。'
        },
        {
          title: '重复练习',
          description: '等孩子玩一会儿后，重复上述步骤。每次训练做3-5轮。',
          tips: '观察孩子状态，如果出现抵触情绪及时停止。'
        }
      ],
      successCriteria: '孩子能主动看向家长眼睛3次以上'
    };
  },

  // 返回
  goBack() {
    if (this.data.timerRunning || this.data.elapsedSeconds > 0) {
      wx.showModal({
        title: '确认退出',
        content: '训练进行中，确定要退出吗？当前进度将不会保存。',
        success: (res) => {
          if (res.confirm) {
            this.stopTimer();
            wx.navigateBack();
          }
        }
      });
    } else {
      wx.navigateBack();
    }
  },

  // ==================== 计时器控制 ====================

  startTimer() {
    if (this.timerInterval) return;
    
    this.setData({ timerRunning: true, timerPaused: false });
    
    this.timerInterval = setInterval(() => {
      const elapsed = this.data.elapsedSeconds + 1;
      this.setData({
        elapsedSeconds: elapsed,
        displayTime: this.formatTime(elapsed)
      });
    }, 1000);
  },

  pauseTimer() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
    this.setData({ timerRunning: false, timerPaused: true });
  },

  resumeTimer() {
    this.startTimer();
  },

  stopTimer() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
    this.setData({ timerRunning: false, timerPaused: false });
  },

  toggleTimer() {
    if (this.data.timerRunning) {
      this.pauseTimer();
    } else {
      this.startTimer();
    }
  },

  formatTime(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  },

  // ==================== 步骤控制 ====================

  prevStep() {
    if (this.data.currentStepIndex > 0) {
      this.setData({
        currentStepIndex: this.data.currentStepIndex - 1
      });
    }
  },

  nextStep() {
    if (this.data.currentStepIndex < this.data.totalSteps - 1) {
      this.setData({
        currentStepIndex: this.data.currentStepIndex + 1
      });
    }
  },

  goToStep(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ currentStepIndex: index });
  },

  // ==================== 成功计数 ====================

  recordSuccess() {
    this.setData({
      successCount: this.data.successCount + 1,
      totalAttempts: this.data.totalAttempts + 1
    });
    
    // 震动反馈
    wx.vibrateShort({ type: 'light' });
    
    wx.showToast({
      title: '记录成功 +1',
      icon: 'success',
      duration: 800
    });
  },

  recordAttempt() {
    this.setData({
      totalAttempts: this.data.totalAttempts + 1
    });
    
    wx.showToast({
      title: '已记录尝试',
      icon: 'none',
      duration: 800
    });
  },

  // ==================== 完成训练 ====================

  showComplete() {
    this.pauseTimer();
    this.setData({ showCompleteModal: true });
  },

  closeCompleteModal() {
    this.setData({ showCompleteModal: false });
  },

  confirmComplete() {
    this.setData({ showCompleteModal: false });
    this.stopTimer();
    
    const { sessionId, elapsedSeconds, successCount, totalAttempts } = this.data;
    const durationMinutes = Math.ceil(elapsedSeconds / 60);
    
    if (sessionId) {
      // 跳转到反馈页面
      wx.navigateTo({
        url: `/pages/training-feedback/training-feedback?sessionId=${sessionId}&duration=${durationMinutes}&success=${successCount}&total=${totalAttempts}`
      });
    } else {
      // 模拟模式
      wx.setStorageSync('trainingCompleted', 'mock');
      wx.navigateBack({ delta: 2 });
    }
  },

  // 继续训练
  continueTraining() {
    this.setData({ showCompleteModal: false });
    this.resumeTimer();
  }
});

