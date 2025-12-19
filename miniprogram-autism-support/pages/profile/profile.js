// pages/profile/profile.js
const app = getApp();
const api = require('../../utils/api');
const badgeTrigger = require('../../utils/badge-trigger');

Page({
  data: {
    isLogin: false,
    userInfo: {
      nickname: '',
      avatar: '',
      mobile: ''
    },
    childProfile: {
      ageRange: ''
    },
    stats: {
      trainingDays: 0,
      posts: 0,
      comments: 0,
      reviews: 0
    },
    // 荣誉系统数据
    honorSummary: {
      greeting: '',
      daysWithUs: 0,
      badges: [],
      hasRelayMemory: false,
      relayMemories: [],
      unreadThankCount: 0
    }
  },

  onLoad() {
    this.loadUserInfo();
  },

  onShow() {
    // 每次显示页面时刷新用户信息
    this.loadUserInfo();
    // 加载荣誉系统数据
    if (app.isLoggedIn()) {
      this.loadHonorSummary();
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadUserInfo().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 加载用户信息
  async loadUserInfo() {
    const isLogin = app.isLoggedIn();
    
    if (isLogin) {
      // 从本地获取用户信息
      const userInfo = wx.getStorageSync('userInfo') || {};
      const childProfile = wx.getStorageSync('childProfile');
      
      this.setData({
        isLogin: true,
        userInfo: {
          nickname: userInfo.nickname || userInfo.name || '未设置昵称',
          avatar: userInfo.avatar || '',
          mobile: userInfo.mobile ? this.maskMobile(userInfo.mobile) : ''
        },
        childProfile: childProfile || { ageRange: '' }
      });

      // 异步刷新用户信息
      this.refreshUserInfoFromServer();
      // 加载用户统计数据
      this.loadUserStats();
    } else {
      this.setData({
        isLogin: false,
        userInfo: {
          nickname: '',
          avatar: '',
          mobile: ''
        }
      });
    }
  },

  // 从服务器刷新用户信息
  async refreshUserInfoFromServer() {
    try {
      const userInfo = await api.getUserInfo();
      if (userInfo) {
        wx.setStorageSync('userInfo', userInfo);
        app.globalData.userInfo = userInfo;
        
        this.setData({
          'userInfo.nickname': userInfo.nickname || userInfo.name || '未设置昵称',
          'userInfo.avatar': userInfo.avatar || '',
          'userInfo.mobile': userInfo.mobile ? this.maskMobile(userInfo.mobile) : ''
        });
      }
    } catch (err) {
      console.warn('刷新用户信息失败:', err);
    }
  },

  // 加载用户统计数据
  async loadUserStats() {
    // TODO: 从服务器获取用户统计数据
    // 暂时使用模拟数据
    this.setData({
      stats: {
        trainingDays: 23,
        posts: 5,
        comments: 12,
        reviews: 3
      }
    });
  },

  // 加载荣誉系统数据
  async loadHonorSummary() {
    try {
      const summary = await api.getHonorSummary();
      if (summary) {
        this.setData({
          honorSummary: {
            greeting: summary.greeting || badgeTrigger.generateGreeting(),
            daysWithUs: summary.daysWithUs || 1,
            badges: summary.badges || [],
            hasRelayMemory: summary.hasRelayMemory || false,
            relayMemories: summary.relayMemories || [],
            unreadThankCount: summary.unreadThankCount || 0
          }
        });
      }
    } catch (err) {
      console.warn('加载荣誉数据失败:', err);
      // 使用本地生成的问候语
      this.setData({
        'honorSummary.greeting': badgeTrigger.generateGreeting(),
        'honorSummary.daysWithUs': 1
      });
    }
  },

  // 手机号脱敏
  maskMobile(mobile) {
    if (!mobile || mobile.length < 11) return mobile;
    return mobile.substring(0, 3) + '****' + mobile.substring(7);
  },

  // 去登录
  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    });
  },

  // 编辑档案
  editProfile() {
    if (!app.checkLogin({ redirectUrl: '/pages/child-profile/child-profile' })) {
      return;
    }
    wx.navigateTo({
      url: '/pages/child-profile/child-profile'
    });
  },

  // 我的训练记录
  goToMyTraining() {
    if (!app.checkLogin()) return;
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 我的帖子
  goToMyPosts() {
    if (!app.checkLogin()) return;
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 我的评论
  goToMyComments() {
    if (!app.checkLogin()) return;
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 我的机构评价
  goToMyReviews() {
    if (!app.checkLogin()) return;
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 我的收藏
  goToMyCollections() {
    if (!app.checkLogin()) return;
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 我的接力
  goToMyRelay() {
    if (!app.checkLogin({ redirectUrl: '/pages/my-relay/my-relay' })) {
      return;
    }
    wx.navigateTo({
      url: '/pages/my-relay/my-relay'
    });
  },

  // 查看所有勋章
  goToAllBadges() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
    // TODO: 跳转到勋章详情页
    // wx.navigateTo({ url: '/pages/badges/badges' });
  },

  // 查看接力记忆
  goToRelayMemories() {
    if (!app.checkLogin({ redirectUrl: '/pages/my-relay/my-relay' })) {
      return;
    }
    wx.navigateTo({
      url: '/pages/my-relay/my-relay'
    });
  },

  // 查看感谢私信
  goToThankMessages() {
    if (!app.checkLogin({ redirectUrl: '/pages/thank-messages/thank-messages' })) {
      return;
    }
    wx.navigateTo({
      url: '/pages/thank-messages/thank-messages'
    });
  },

  // 视觉计时器
  goToTimer() {
    wx.navigateTo({
      url: '/pages/timer/timer'
    });
  },

  // 训练清单
  goToChecklist() {
    if (!app.checkLogin()) return;
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 训练日历
  goToCalendar() {
    if (!app.checkLogin()) return;
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 观察笔记
  goToNotes() {
    if (!app.checkLogin()) return;
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 加入微信群
  joinWechatGroup() {
    wx.showModal({
      title: '加入微信群',
      content: '扫描二维码加入同行家长微信群，与更多家长交流经验',
      confirmText: '查看二维码',
      success: (res) => {
        if (res.confirm) {
          wx.showToast({ title: '二维码功能开发中', icon: 'none' });
        }
      }
    });
  },

  // 意见反馈
  goToFeedback() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 关于我们
  goToAbout() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 设置
  goToSettings() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 退出登录
  handleLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      confirmText: '退出',
      confirmColor: '#FF6B6B',
      success: async (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '退出中...', mask: true });
          
          try {
            await app.logout();
            wx.hideLoading();
            wx.showToast({ title: '已退出登录', icon: 'success' });
            
            // 刷新页面状态
            this.loadUserInfo();
          } catch (err) {
            wx.hideLoading();
            wx.showToast({ title: '退出失败', icon: 'none' });
          }
        }
      }
    });
  }
});
