// pages/tip-detail/tip-detail.js
Page({
  data: {
    tipId: '',
    tip: null,
    relatedTips: []
  },

  onLoad(options) {
    this.setData({ tipId: options.id });
    this.loadTipDetail();
    this.loadRelatedTips();
  },

  loadTipDetail() {
    const mockTip = {
      id: this.data.tipId,
      title: '如何建立眼神接触？',
      date: '2025年1月15日',
      content: '<p>在孩子感兴趣的活动中，把玩具举到眼睛旁边，等待孩子看向你。</p><p><strong>关键要点：</strong></p><p>1. 不要强迫，让它自然发生</p><p>2. 使用孩子喜欢的玩具或零食作为动机</p><p>3. 当孩子看向你时，立即给予奖励</p><p>4. 每天多次进行，但每次时间不要太长</p>',
      author: '星星家长'
    };
    this.setData({ tip: mockTip });
  },

  loadRelatedTips() {
    const mockRelated = [
      { id: '2', title: '如何训练呼名反应？', date: '1月14日' },
      { id: '3', title: '情绪爆发时怎么办？', date: '1月13日' },
      { id: '4', title: '入园前的准备清单', date: '1月12日' }
    ];
    this.setData({ relatedTips: mockRelated });
  },

  goToTip(e) {
    const id = e.currentTarget.dataset.id;
    wx.redirectTo({ url: `/pages/tip-detail/tip-detail?id=${id}` });
  },

  shareTip() {
    // 触发分享
  },

  goBack() {
    wx.navigateBack();
  },

  onShareAppMessage() {
    return {
      title: this.data.tip.title,
      path: `/pages/tip-detail/tip-detail?id=${this.data.tipId}`
    };
  }
});

