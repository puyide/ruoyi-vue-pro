// pages/relay-detail/relay-detail.js
const app = getApp();

Page({
  data: {
    itemId: '',
    item: null,
    showApplyModal: false,
    showSuccessModal: false,
    applyMessage: ''
  },

  onLoad(options) {
    this.setData({ itemId: options.id });
    this.loadItemDetail();
  },

  // 加载好物详情
  loadItemDetail() {
    // 模拟数据
    const mockItem = {
      id: this.data.itemId,
      title: '感统平衡板',
      condition: '8成新',
      type: '感统训练',
      ageGroup: '3-6岁',
      method: 'give',
      methodText: '赠送',
      delivery: 'face_to_face',
      deliveryText: '面交',
      photos: [],
      description: '用过半年，孩子已经升级训练了，平衡板保养得很好，没有磕碰。适合刚开始做感统训练的孩子使用。',
      giver: {
        id: 'user1',
        nickname: '星星妈妈'
      },
      publishTime: '2025/01/10',
      status: 'available'
    };

    this.setData({ item: mockItem });
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      current: url,
      urls: this.data.item.photos
    });
  },

  // 发起接力申请
  applyRelay() {
    // 检查是否登录
    if (!app.globalData.isLogin) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再申请',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            // 跳转到登录
          }
        }
      });
      return;
    }

    // 检查是否是自己发布的
    // TODO: 实际需要比较用户ID
    
    this.setData({ showApplyModal: true });
  },

  // 隐藏申请弹窗
  hideApplyModal() {
    this.setData({ showApplyModal: false, applyMessage: '' });
  },

  // 申请内容输入
  onApplyInput(e) {
    this.setData({ applyMessage: e.detail.value });
  },

  // 提交申请
  submitApply() {
    // 检查敏感信息（简单检测手机号和地址关键词）
    const message = this.data.applyMessage;
    const phonePattern = /1[3-9]\d{9}/;
    const sensitiveWords = ['地址', '小区', '街道', '号楼', '单元'];
    
    if (phonePattern.test(message)) {
      wx.showToast({
        title: '请勿填写手机号',
        icon: 'none'
      });
      return;
    }
    
    for (const word of sensitiveWords) {
      if (message.includes(word)) {
        wx.showToast({
          title: '请勿填写地址信息',
          icon: 'none'
        });
        return;
      }
    }

    wx.showLoading({ title: '提交中...' });

    // 模拟提交
    setTimeout(() => {
      wx.hideLoading();
      this.setData({
        showApplyModal: false,
        showSuccessModal: true,
        applyMessage: ''
      });
    }, 1000);
  },

  // 隐藏成功弹窗
  hideSuccessModal() {
    this.setData({ showSuccessModal: false });
  },

  // 跳转到我的接力
  goToMyRelay() {
    this.setData({ showSuccessModal: false });
    wx.navigateTo({
      url: '/pages/my-relay/my-relay'
    });
  },

  // 分享
  shareItem() {
    // 触发分享
  },

  // 返回
  goBack() {
    wx.navigateBack();
  },

  // 分享给朋友
  onShareAppMessage() {
    return {
      title: `${this.data.item.title} - 成长接力`,
      path: `/pages/relay-detail/relay-detail?id=${this.data.itemId}`
    };
  }
});

