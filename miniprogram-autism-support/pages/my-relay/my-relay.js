// pages/my-relay/my-relay.js
const app = getApp();

Page({
  data: {
    currentTab: 'received',
    receivedCount: 2,
    receivedRequests: [],
    sentRequests: [],
    publishedItems: [],

    showTransferModal: false,
    transferMethod: 'face_to_face',
    trackingNumber: '',
    currentTransferId: '',

    showThankModal: false,
    thankMessage: '',
    thankPublic: true
  },

  onLoad() {
    this.loadData();
  },

  // 加载数据
  loadData() {
    // 模拟数据
    this.setData({
      receivedRequests: [
        {
          id: '1',
          itemId: 'item1',
          itemTitle: '语言图片卡（全套）',
          itemPhoto: '',
          requester: { nickname: '阳阳爸爸', wechat: 'yangyang_dad' },
          message: '孩子正在进行语言训练，这套卡片非常适合，希望能接力使用',
          status: 'pending',
          statusText: '待处理',
          timeAgo: '2小时前'
        },
        {
          id: '2',
          itemId: 'item1',
          itemTitle: '语言图片卡（全套）',
          itemPhoto: '',
          requester: { nickname: '小雨妈妈', wechat: 'xiaoyu_mom' },
          message: '我们家孩子4岁，正好需要语言训练',
          status: 'accepted',
          statusText: '已同意',
          timeAgo: '1天前',
          contactExchanged: true
        }
      ],
      sentRequests: [
        {
          id: '3',
          itemId: 'item2',
          itemTitle: '感统平衡板',
          itemPhoto: '',
          giver: { nickname: '星星妈妈', wechat: 'star_mom' },
          message: '孩子需要感统训练，这个平衡板很适合',
          status: 'accepted',
          statusText: '已同意',
          myWechatFilled: false,
          myWechat: ''
        },
        {
          id: '4',
          itemId: 'item3',
          itemTitle: '注意力训练卡',
          itemPhoto: '',
          giver: { nickname: '豆豆妈' },
          message: '',
          status: 'pending',
          statusText: '等待中'
        }
      ],
      publishedItems: [
        {
          id: 'item1',
          title: '语言图片卡（全套）',
          photos: [],
          status: 'available',
          statusText: '接力中',
          requestCount: 2
        }
      ]
    });
  },

  // 切换标签
  switchTab(e) {
    this.setData({ currentTab: e.currentTarget.dataset.tab });
  },

  // 同意申请
  acceptRequest(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '同意接力',
      content: '同意后，双方确认即可交换联系方式',
      success: (res) => {
        if (res.confirm) {
          // 更新状态
          const requests = this.data.receivedRequests.map(r => {
            if (r.id === id) {
              return { ...r, status: 'accepted', statusText: '已同意' };
            }
            return r;
          });
          this.setData({
            receivedRequests: requests,
            receivedCount: this.data.receivedCount - 1
          });
          wx.showToast({ title: '已同意', icon: 'success' });
        }
      }
    });
  },

  // 拒绝申请
  rejectRequest(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '拒绝申请',
      content: '确定要拒绝这个接力申请吗？',
      success: (res) => {
        if (res.confirm) {
          const requests = this.data.receivedRequests.map(r => {
            if (r.id === id) {
              return { ...r, status: 'rejected', statusText: '已拒绝' };
            }
            return r;
          });
          this.setData({
            receivedRequests: requests,
            receivedCount: this.data.receivedCount - 1
          });
          wx.showToast({ title: '已拒绝', icon: 'none' });
        }
      }
    });
  },

  // 微信号输入
  onWechatInput(e) {
    const id = e.currentTarget.dataset.id;
    const requests = this.data.sentRequests.map(r => {
      if (r.id === id) {
        return { ...r, myWechat: e.detail.value };
      }
      return r;
    });
    this.setData({ sentRequests: requests });
  },

  // 确认交换
  confirmExchange(e) {
    const id = e.currentTarget.dataset.id;
    const request = this.data.sentRequests.find(r => r.id === id);
    
    if (!request.myWechat.trim()) {
      wx.showToast({ title: '请输入微信号', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '提交中...' });
    
    setTimeout(() => {
      wx.hideLoading();
      const requests = this.data.sentRequests.map(r => {
        if (r.id === id) {
          return { ...r, myWechatFilled: true };
        }
        return r;
      });
      this.setData({ sentRequests: requests });
      
      // 显示感谢弹窗
      this.setData({ showThankModal: true });
    }, 1000);
  },

  // 复制微信号
  copyWechat(e) {
    const wechat = e.currentTarget.dataset.wechat;
    wx.setClipboardData({
      data: wechat,
      success: () => {
        wx.showToast({ title: '已复制', icon: 'success' });
      }
    });
  },

  // 显示交接弹窗
  showTransferModal(id) {
    this.setData({
      showTransferModal: true,
      currentTransferId: id
    });
  },

  hideTransferModal() {
    this.setData({ showTransferModal: false });
  },

  selectTransferMethod(e) {
    this.setData({ transferMethod: e.currentTarget.dataset.value });
  },

  onTrackingInput(e) {
    this.setData({ trackingNumber: e.detail.value });
  },

  confirmTransfer() {
    wx.showToast({ title: '已确认', icon: 'success' });
    this.setData({ showTransferModal: false });
  },

  // 感谢弹窗
  hideThankModal() {
    this.setData({ showThankModal: false });
  },

  onThankInput(e) {
    this.setData({ thankMessage: e.detail.value });
  },

  toggleThankPublic(e) {
    this.setData({ thankPublic: e.detail.value });
  },

  submitThank() {
    wx.showToast({ title: '感谢已发送', icon: 'success' });
    this.setData({
      showThankModal: false,
      thankMessage: ''
    });
  },

  // 编辑好物
  editItem(e) {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 下架好物
  deleteItem(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '下架好物',
      content: '确定要下架这件好物吗？下架后将不再显示',
      success: (res) => {
        if (res.confirm) {
          const items = this.data.publishedItems.filter(i => i.id !== id);
          this.setData({ publishedItems: items });
          wx.showToast({ title: '已下架', icon: 'success' });
        }
      }
    });
  },

  // 跳转
  goToItem(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/relay-detail/relay-detail?id=${id}`
    });
  },

  goToRelay() {
    wx.navigateTo({
      url: '/pages/relay/relay'
    });
  },

  goToPublish() {
    wx.navigateTo({
      url: '/pages/relay-publish/relay-publish'
    });
  },

  goBack() {
    wx.navigateBack();
  }
});

