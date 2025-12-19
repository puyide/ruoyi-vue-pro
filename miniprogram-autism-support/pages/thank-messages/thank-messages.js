// pages/thank-messages/thank-messages.js
const app = getApp();
const api = require('../../utils/api');
const badgeTrigger = require('../../utils/badge-trigger');

Page({
  data: {
    messages: [],
    loading: true,
    isEmpty: false
  },

  onLoad() {
    this.loadMessages();
  },

  onShow() {
    // 每次显示时刷新
    this.loadMessages();
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadMessages().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 加载感谢私信
  async loadMessages() {
    this.setData({ loading: true });

    try {
      const messages = await api.getThankMessages();
      
      // 格式化时间
      const formattedMessages = (messages || []).map(msg => ({
        ...msg,
        timeText: badgeTrigger.formatRelativeTime(msg.createTime)
      }));

      this.setData({
        messages: formattedMessages,
        isEmpty: formattedMessages.length === 0,
        loading: false
      });

      // 如果有未读消息，标记为已读
      if (formattedMessages.some(m => !m.isRead)) {
        this.markAllAsRead();
      }
    } catch (err) {
      console.error('加载感谢私信失败:', err);
      this.setData({
        messages: [],
        isEmpty: true,
        loading: false
      });
    }
  },

  // 标记所有为已读
  async markAllAsRead() {
    try {
      await api.markAllThankMessagesAsRead();
      // 更新本地状态
      const messages = this.data.messages.map(m => ({
        ...m,
        isRead: true
      }));
      this.setData({ messages });
    } catch (err) {
      console.warn('标记已读失败:', err);
    }
  },

  // 点击消息
  onMessageTap(e) {
    const { id, itemid } = e.currentTarget.dataset;
    
    // 可以跳转到物品详情
    if (itemid) {
      wx.navigateTo({
        url: `/pages/relay-detail/relay-detail?id=${itemid}`
      });
    }
  },

  // 返回
  goBack() {
    wx.navigateBack();
  }
});

