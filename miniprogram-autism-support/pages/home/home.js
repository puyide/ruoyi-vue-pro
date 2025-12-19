// pages/home/home.js
const app = getApp();

Page({
  data: {
    dailyTip: {
      id: '1',
      title: '如何建立眼神接触？',
      advice: '在孩子感兴趣的活动中，把玩具举到眼睛旁边，等待孩子看向你。记住：不要强迫，让它自然发生。',
      author: '星星家长'
    },
    featuredPosts: [
      {
        id: '1',
        title: '孩子叫名字不看我，我是这样一步步改善的',
        tags: ['语言训练', '3-6岁'],
        aiSummary: '通过在孩子喜欢的活动中进行呼名训练，配合视觉提示和即时强化，3周内有明显改善...',
        viewCount: 1234,
        commentCount: 56,
        timeAgo: '2小时前'
      },
      {
        id: '2',
        title: '情绪崩溃时的应对方法，分享我们的经验',
        tags: ['情绪管理', '入园准备'],
        aiSummary: '识别情绪爆发前兆，提前介入；使用视觉计时器帮助孩子理解等待时间...',
        viewCount: 892,
        commentCount: 34,
        timeAgo: '5小时前'
      },
      {
        id: '3',
        title: '感统训练在家做，简单又有效的10个游戏',
        tags: ['感统训练', '居家干预'],
        aiSummary: '利用家里常见物品进行前庭觉、本体觉训练，每天15分钟坚持效果明显...',
        viewCount: 2156,
        commentCount: 89,
        timeAgo: '1天前'
      }
    ]
  },

  onLoad() {
    this.loadDailyTip();
    this.loadFeaturedPosts();
  },

  onShow() {
    // 页面显示时更新
  },

  onPullDownRefresh() {
    this.loadDailyTip();
    this.loadFeaturedPosts();
    setTimeout(() => {
      wx.stopPullDownRefresh();
    }, 1000);
  },

  // 加载今日锦囊
  loadDailyTip() {
    // TODO: 从API加载
  },

  // 加载精选帖子
  loadFeaturedPosts() {
    // TODO: 从API加载
  },

  // 跳转到搜索页
  goToSearch() {
    wx.navigateTo({
      url: '/pages/search/search'
    });
  },

  // 跳转到锦囊详情
  goToTipDetail() {
    wx.navigateTo({
      url: `/pages/tip-detail/tip-detail?id=${this.data.dailyTip.id}`
    });
  },

  // 跳转到训练助手
  goToTraining() {
    wx.switchTab({
      url: '/pages/training/training'
    });
  },

  // 跳转到社区
  goToCommunity() {
    wx.switchTab({
      url: '/pages/community/community'
    });
  },

  // 跳转到小组
  goToGroup() {
    wx.switchTab({
      url: '/pages/group/group'
    });
  },

  // 跳转到成长接力
  goToRelay() {
    wx.navigateTo({
      url: '/pages/relay/relay'
    });
  },

  // 跳转到资源黄页
  goToResource() {
    wx.navigateTo({
      url: '/pages/resource/resource'
    });
  },

  // 跳转到帖子详情
  goToPostDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/post-detail/post-detail?id=${id}`
    });
  }
});

