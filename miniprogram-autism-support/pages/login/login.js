// pages/login/login.js
const app = getApp();
const api = require('../../utils/api');

// 短信场景枚举
const SMS_SCENE = {
  MEMBER_LOGIN: 1,          // 会员登录
  MEMBER_UPDATE_MOBILE: 2,  // 修改手机号
  MEMBER_RESET_PASSWORD: 3, // 重置密码
  MEMBER_UPDATE_PASSWORD: 4 // 修改密码
};

Page({
  data: {
    loginType: 'wechat',    // 登录类型：wechat / phone
    mobile: '',             // 手机号
    smsCode: '',            // 验证码
    agreed: false,          // 是否同意协议
    loading: false,         // 登录中
    sendingCode: false,     // 发送验证码中
    countdown: 0,           // 倒计时
    wxLoginCode: '',        // 微信登录 code
    wxState: ''             // 微信登录 state
  },

  // 计算属性：是否可以发送验证码
  get canSendSms() {
    return this.data.mobile.length === 11 && this.data.countdown === 0;
  },

  // 计算属性：是否可以登录
  get canLogin() {
    return this.data.mobile.length === 11 && this.data.smsCode.length >= 4 && this.data.agreed;
  },

  onLoad(options) {
    // 如果有回调页面，保存起来
    if (options.redirect) {
      this.redirectUrl = decodeURIComponent(options.redirect);
    }
    // 预先获取微信登录 code
    this.getWxLoginCode();
  },

  onShow() {
    // 更新计算属性到 data
    this.updateComputedData();
  },

  // 更新计算属性
  updateComputedData() {
    this.setData({
      canSendSms: this.data.mobile.length === 11 && this.data.countdown === 0,
      canLogin: this.data.mobile.length === 11 && this.data.smsCode.length >= 4 && this.data.agreed
    });
  },

  // 获取微信登录 code
  getWxLoginCode() {
    wx.login({
      success: (res) => {
        if (res.code) {
          this.setData({
            wxLoginCode: res.code,
            wxState: this.generateState()
          });
        }
      }
    });
  },

  // 生成 state
  generateState() {
    return 'state_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
  },

  // 切换登录方式
  switchLoginType(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ loginType: type });
    // 切换时重新获取 code
    if (type === 'wechat') {
      this.getWxLoginCode();
    }
  },

  // 手机号输入
  onMobileInput(e) {
    this.setData({ mobile: e.detail.value });
    this.updateComputedData();
  },

  // 清空手机号
  clearMobile() {
    this.setData({ mobile: '' });
    this.updateComputedData();
  },

  // 验证码输入
  onCodeInput(e) {
    this.setData({ smsCode: e.detail.value });
    this.updateComputedData();
  },

  // 切换协议同意状态
  toggleAgreement() {
    this.setData({ agreed: !this.data.agreed });
    this.updateComputedData();
  },

  // 查看用户协议
  viewUserAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '用户协议内容正在完善中...',
      showCancel: false
    });
  },

  // 查看隐私政策
  viewPrivacyPolicy() {
    wx.showModal({
      title: '隐私政策',
      content: '隐私政策内容正在完善中...',
      showCancel: false
    });
  },

  // 发送验证码
  async sendSmsCode() {
    if (!this.data.canSendSms || this.data.sendingCode) return;

    // 验证手机号格式
    if (!/^1[3-9]\d{9}$/.test(this.data.mobile)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }

    this.setData({ sendingCode: true });

    try {
      await api.sendSmsCode({
        mobile: this.data.mobile,
        scene: SMS_SCENE.MEMBER_LOGIN
      });

      wx.showToast({ title: '验证码已发送', icon: 'success' });
      
      // 开始倒计时
      this.startCountdown();
    } catch (err) {
      wx.showToast({ title: err.msg || '发送失败', icon: 'none' });
    } finally {
      this.setData({ sendingCode: false });
    }
  },

  // 开始倒计时
  startCountdown() {
    this.setData({ countdown: 60 });
    
    this.countdownTimer = setInterval(() => {
      if (this.data.countdown > 0) {
        this.setData({ countdown: this.data.countdown - 1 });
        this.updateComputedData();
      } else {
        clearInterval(this.countdownTimer);
      }
    }, 1000);
  },

  // 微信手机号授权回调
  async onGetPhoneNumber(e) {
    if (e.detail.errMsg !== 'getPhoneNumber:ok') {
      wx.showToast({ title: '需要授权手机号才能登录', icon: 'none' });
      return;
    }

    // 检查是否同意协议
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意用户协议', icon: 'none' });
      return;
    }

    // 获取手机号 code
    const phoneCode = e.detail.code;
    if (!phoneCode) {
      wx.showToast({ title: '获取手机号失败', icon: 'none' });
      return;
    }

    // 确保有 loginCode
    if (!this.data.wxLoginCode) {
      await this.getWxLoginCode();
      // 等待获取 code
      await new Promise(resolve => setTimeout(resolve, 500));
    }

    this.setData({ loading: true });

    try {
      const result = await api.weixinMiniAppLogin({
        phoneCode: phoneCode,
        loginCode: this.data.wxLoginCode,
        state: this.data.wxState
      });

      // 登录成功，保存信息
      this.handleLoginSuccess(result);
    } catch (err) {
      console.error('微信登录失败:', err);
      wx.showToast({ title: err.msg || '登录失败', icon: 'none' });
      // 重新获取 code
      this.getWxLoginCode();
    } finally {
      this.setData({ loading: false });
    }
  },

  // 手机验证码登录
  async handleSmsLogin() {
    if (!this.data.canLogin || this.data.loading) return;

    // 验证手机号格式
    if (!/^1[3-9]\d{9}$/.test(this.data.mobile)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }

    // 验证验证码格式
    if (!/^\d{4,6}$/.test(this.data.smsCode)) {
      wx.showToast({ title: '请输入正确的验证码', icon: 'none' });
      return;
    }

    // 检查协议
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意用户协议', icon: 'none' });
      return;
    }

    this.setData({ loading: true });

    try {
      // 构建请求参数
      const params = {
        mobile: this.data.mobile,
        code: this.data.smsCode
      };

      // 如果有微信 code，可以同时绑定微信
      if (this.data.wxLoginCode) {
        params.socialType = 34; // 微信小程序
        params.socialCode = this.data.wxLoginCode;
        params.socialState = this.data.wxState;
      }

      const result = await api.smsLogin(params);

      // 登录成功，保存信息
      this.handleLoginSuccess(result);
    } catch (err) {
      console.error('手机登录失败:', err);
      wx.showToast({ title: err.msg || '登录失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 处理登录成功
  async handleLoginSuccess(result) {
    // 保存 token
    wx.setStorageSync('token', result.accessToken);
    wx.setStorageSync('refreshToken', result.refreshToken);
    if (result.openid) {
      wx.setStorageSync('openid', result.openid);
    }

    // 更新全局状态
    app.globalData.isLogin = true;

    // 获取用户信息
    try {
      const userInfo = await api.getUserInfo();
      wx.setStorageSync('userInfo', userInfo);
      app.globalData.userInfo = userInfo;
    } catch (err) {
      console.warn('获取用户信息失败:', err);
    }

    wx.showToast({ title: '登录成功', icon: 'success' });

    // 延迟跳转
    setTimeout(() => {
      if (this.redirectUrl) {
        wx.redirectTo({ url: this.redirectUrl });
      } else {
        // 返回上一页或跳转首页
        const pages = getCurrentPages();
        if (pages.length > 1) {
          wx.navigateBack();
        } else {
          wx.switchTab({ url: '/pages/home/home' });
        }
      }
    }, 1500);
  },

  onUnload() {
    // 清除倒计时
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
  }
});

