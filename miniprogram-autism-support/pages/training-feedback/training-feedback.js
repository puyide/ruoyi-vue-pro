// pages/training-feedback/training-feedback.js
const api = require('../../utils/api');

Page({
  data: {
    // 参数
    sessionId: null,
    duration: 0,
    successCount: 0,
    totalAttempts: 0,
    
    // 反馈数据
    childMood: 0, // 0-未选择, 1-好, 2-一般, 3-差
    parentComment: '',
    
    // AI反馈
    aiFeedback: null,
    loadingFeedback: false,
    
    // 提交状态
    submitting: false,
    submitted: false
  },

  onLoad(options) {
    this.setData({
      sessionId: options.sessionId || null,
      duration: parseInt(options.duration) || 0,
      successCount: parseInt(options.success) || 0,
      totalAttempts: parseInt(options.total) || 0
    });
  },

  // 选择心情
  selectMood(e) {
    const mood = parseInt(e.currentTarget.dataset.mood);
    this.setData({ childMood: mood });
  },

  // 输入评论
  onCommentInput(e) {
    this.setData({ parentComment: e.detail.value });
  },

  // 提交反馈
  async submitFeedback() {
    const { sessionId, childMood, parentComment, duration, successCount, totalAttempts } = this.data;
    
    if (childMood === 0) {
      wx.showToast({ title: '请选择孩子的心情状态', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });

    try {
      // 提交训练日志
      const log = await api.createTrainingLog({
        sessionId: sessionId,
        completed: true,
        successCount: successCount,
        totalCount: totalAttempts,
        childMood: childMood,
        durationMinutes: duration,
        parentComment: parentComment
      });

      this.setData({ 
        submitted: true,
        submitting: false 
      });

      // 获取AI反馈
      this.loadAiFeedback(log.id);
    } catch (err) {
      console.error('提交反馈失败:', err);
      this.setData({ submitting: false });
      
      // 模拟模式
      this.setData({ 
        submitted: true,
        aiFeedback: this.getMockFeedback()
      });
    }
  },

  // 加载AI反馈
  async loadAiFeedback(logId) {
    this.setData({ loadingFeedback: true });
    
    try {
      const feedback = await api.getTrainingFeedback(logId);
      this.setData({ 
        aiFeedback: feedback,
        loadingFeedback: false 
      });
    } catch (err) {
      console.error('获取AI反馈失败:', err);
      this.setData({ 
        aiFeedback: this.getMockFeedback(),
        loadingFeedback: false 
      });
    }
  },

  getMockFeedback() {
    const { successCount, totalAttempts, childMood } = this.data;
    const successRate = totalAttempts > 0 ? Math.round(successCount / totalAttempts * 100) : 0;
    
    let summary = '';
    let suggestions = [];
    let encouragement = '';

    if (successRate >= 70) {
      summary = '今天的训练非常棒！孩子表现出了很好的进步。';
      encouragement = '继续保持这样的训练频率，很快就能看到更大的进步！';
      suggestions = [
        '可以考虑适当增加难度，挑战更高目标',
        '在日常生活中多创造练习机会',
        '记录孩子的小进步，及时给予鼓励'
      ];
    } else if (successRate >= 40) {
      summary = '今天的训练有一定成效，孩子正在努力适应。';
      encouragement = '每一次尝试都是进步，请给孩子更多耐心和鼓励！';
      suggestions = [
        '可以尝试将训练拆分为更小的步骤',
        '增加强化物的吸引力',
        '选择孩子状态好的时间进行训练'
      ];
    } else {
      summary = '今天的训练可能遇到了一些困难，不要气馁。';
      encouragement = '学习需要时间，相信孩子会越来越好！';
      suggestions = [
        '建议降低难度，重新建立成功体验',
        '检查训练环境是否有干扰因素',
        '可以暂停1-2天，让孩子休息调整'
      ];
    }

    if (childMood === 3) { // 心情差
      suggestions.push('注意观察孩子的情绪变化，必要时暂停训练');
    }

    return {
      summary,
      suggestions,
      encouragement,
      nextSteps: '建议明天继续进行同类型训练，巩固今天的成果'
    };
  },

  // 返回训练首页
  goHome() {
    wx.setStorageSync('trainingCompleted', this.data.sessionId || 'done');
    wx.navigateBack({ delta: 3 }); // 返回训练首页
  },

  // 查看社区
  viewCommunity() {
    wx.switchTab({ url: '/pages/community/community' });
  },

  // 再训练一次
  trainAgain() {
    wx.navigateBack({ delta: 2 }); // 返回训练详情页
  }
});

