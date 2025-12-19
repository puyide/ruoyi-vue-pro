// app.js
const api = require('./utils/api');

App({
  globalData: {
    userInfo: null,
    childProfile: null,
    isLogin: false,
    openid: null,
    systemInfo: null,
    statusBarHeight: 0,
    safeAreaBottom: 0
  },

  onLaunch() {
    // 检查登录状态
    this.checkLoginStatus();
    
    // 获取系统信息
    try {
      const systemInfo = wx.getSystemInfoSync();
      this.globalData.systemInfo = systemInfo;
      this.globalData.statusBarHeight = systemInfo.statusBarHeight;
      this.globalData.safeAreaBottom = systemInfo.screenHeight - systemInfo.safeArea.bottom;
    } catch (e) {
      console.warn('获取系统信息失败:', e);
    }
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (token) {
      this.globalData.isLogin = true;
      this.globalData.userInfo = userInfo;
      this.globalData.childProfile = wx.getStorageSync('childProfile');
      this.globalData.openid = wx.getStorageSync('openid');
      
      // 验证 token 有效性并刷新用户信息
      this.refreshUserInfo();
    }
  },

  /**
   * 刷新用户信息
   */
  async refreshUserInfo() {
    try {
      const userInfo = await api.getUserInfo();
      wx.setStorageSync('userInfo', userInfo);
      this.globalData.userInfo = userInfo;
      return userInfo;
    } catch (err) {
      console.warn('刷新用户信息失败:', err);
      // 如果是认证失败，清除登录状态
      if (err.code === 401) {
        this.logout(false);
      }
      return null;
    }
  },

  /**
   * 检查是否需要登录
   * @param {Object} options - 配置选项
   * @param {boolean} options.showModal - 是否显示提示弹窗
   * @param {string} options.redirectUrl - 登录后跳转的页面
   * @returns {boolean} 是否已登录
   */
  checkLogin(options = {}) {
    const { showModal = true, redirectUrl = '' } = options;
    
    if (this.globalData.isLogin) {
      return true;
    }

    if (showModal) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再操作',
        confirmText: '去登录',
        cancelText: '取消',
        success: (res) => {
          if (res.confirm) {
            const url = redirectUrl 
              ? `/pages/login/login?redirect=${encodeURIComponent(redirectUrl)}`
              : '/pages/login/login';
            wx.navigateTo({ url });
          }
        }
      });
    }

    return false;
  },

  /**
   * 跳转到登录页
   * @param {string} redirectUrl - 登录后跳转的页面
   */
  goLogin(redirectUrl = '') {
    const url = redirectUrl 
      ? `/pages/login/login?redirect=${encodeURIComponent(redirectUrl)}`
      : '/pages/login/login';
    wx.navigateTo({ url });
  },

  /**
   * 退出登录
   * @param {boolean} callApi - 是否调用后端接口
   */
  async logout(callApi = true) {
    // 调用后端退出接口
    if (callApi) {
      try {
        await api.logout();
      } catch (e) {
        console.warn('退出登录接口调用失败:', e);
      }
    }

    // 清除本地存储
    wx.removeStorageSync('token');
    wx.removeStorageSync('refreshToken');
    wx.removeStorageSync('userInfo');
    wx.removeStorageSync('openid');
    
    // 重置全局状态
    this.globalData.isLogin = false;
    this.globalData.userInfo = null;
    this.globalData.openid = null;

    return true;
  },

  /**
   * 请求封装（兼容旧代码）
   */
  request(options) {
    return api.request(options);
  },

  /**
   * 显示提示
   */
  showToast(title, icon = 'none') {
    wx.showToast({
      title,
      icon,
      duration: 2000
    });
  },

  /**
   * 显示加载
   */
  showLoading(title = '加载中...') {
    wx.showLoading({
      title,
      mask: true
    });
  },

  /**
   * 隐藏加载
   */
  hideLoading() {
    wx.hideLoading();
  },

  /**
   * 获取当前用户信息
   * @returns {Object|null}
   */
  getUserInfo() {
    return this.globalData.userInfo;
  },

  /**
   * 判断是否已登录
   * @returns {boolean}
   */
  isLoggedIn() {
    return this.globalData.isLogin;
  }
});
