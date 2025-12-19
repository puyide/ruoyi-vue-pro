// pages/group-detail/group-detail.js
const app = getApp();
const api = require('../../utils/api');

Page({
  data: {
    groupId: '',
    group: null,
    loading: true,
    // 今日话题
    todayTopic: {
      question: '今天孩子语言训练遇到什么难点？',
      examples: [
        '孩子不愿意模仿发音',
        '注意力很难集中',
        '进步比较慢有些焦虑'
      ]
    },
    // 消息列表
    messages: [],
    inputText: '',
    scrollToView: '',
    hasMore: true,
    // 二维码弹窗
    showQrModal: false,
    // 交流区确认弹窗
    showChatConfirmModal: false,
    // 已退出交流区提示
    showLeftChatTip: false
  },

  onLoad(options) {
    this.setData({ groupId: options.id });
    this.loadGroupDetail();
    this.loadMessages();
  },

  onShow() {
    // 刷新小组详情（可能用户刚加入/退出）
    if (this.data.groupId) {
      this.loadGroupDetail();
    }
  },

  // 加载小组详情
  async loadGroupDetail() {
    try {
      const group = await api.getPeerGroupDetail(this.data.groupId);
      this.setData({ 
        group,
        loading: false
      });
    } catch (error) {
      console.error('加载小组详情失败:', error);
      
      // 使用 Mock 数据
      this.loadMockGroupDetail();
    }
  },

  // Mock 小组详情
  loadMockGroupDetail() {
    const mockGroup = {
      id: this.data.groupId,
      name: '3-6岁 · 语言发展小组',
      icon: '💬',
      theme: 'orange',
      memberCount: 101,
      maxMembers: 200,
      description: '专注3-6岁儿童语言发展训练的家长互助小组',
      joined: true,
      myRole: 'member',
      joinMode: 0,
      // 微信群二维码
      wechatQrUrl: '',
      wechatQrExpire: null,
      wecomUrl: '',
      // NodeBB
      nodebbCategoryId: null
    };
    this.setData({ 
      group: mockGroup,
      loading: false
    });
  },

  // 加载消息
  loadMessages() {
    const mockMessages = [
      {
        id: '1',
        author: { nickname: '星星妈妈' },
        content: '今天练眼神跟随，孩子比昨天有进步了！能坚持看我3秒钟了。',
        timeAgo: '10分钟前',
        likeCount: 5,
        isMine: false
      },
      {
        id: '2',
        author: { nickname: '阳阳爸爸' },
        content: '我们家也是，最近用了新的方法，把玩具举到眼睛旁边，效果不错。',
        timeAgo: '8分钟前',
        likeCount: 3,
        isMine: false
      },
      {
        id: '3',
        author: { nickname: '小雨妈妈' },
        content: '请问大家训练时长是多少？我们每次只能坚持5分钟左右。',
        timeAgo: '5分钟前',
        likeCount: 2,
        isMine: false
      },
      {
        id: '4',
        author: { nickname: '我' },
        content: '5分钟已经很好了！我们刚开始的时候只有1-2分钟，慢慢来。',
        timeAgo: '3分钟前',
        likeCount: 8,
        isMine: true
      },
      {
        id: '5',
        author: { nickname: '豆豆妈' },
        content: '是的，不要着急，每个孩子进度不一样。我们坚持了3个月才有明显进步。',
        timeAgo: '1分钟前',
        likeCount: 12,
        isMine: false
      }
    ];

    this.setData({ 
      messages: mockMessages,
      scrollToView: `msg-${mockMessages[mockMessages.length - 1].id}`
    });
  },

  // 加载更多消息
  loadMoreMessages() {
    if (!this.data.hasMore) return;
    
    wx.showToast({
      title: '加载中...',
      icon: 'loading'
    });

    setTimeout(() => {
      this.setData({ hasMore: false });
    }, 500);
  },

  // 返回
  goBack() {
    wx.navigateBack();
  },

  // 显示小组菜单
  showGroupMenu() {
    const { group } = this.data;
    
    const items = ['小组设置', '查看成员'];
    
    // 添加交流空间入口（使用合规文案）
    if (group.wechatQrUrl || group.wecomUrl) {
      items.push('去和大家聊聊');
    }
    
    // 添加论坛入口
    if (group.nodebbCategoryId) {
      items.push('打开小组交流区');
    }
    
    items.push(group.joined ? '退出小组' : '加入小组');

    wx.showActionSheet({
      itemList: items,
      success: (res) => {
        const selectedItem = items[res.tapIndex];
        
        if (selectedItem === '去和大家聊聊') {
          this.showChatConfirm();
        } else if (selectedItem === '打开小组交流区') {
          this.goToNodeBB();
        } else if (selectedItem === '退出小组') {
          this.confirmLeaveGroup();
        } else if (selectedItem === '加入小组') {
          this.joinGroup();
        }
      }
    });
  },

  // 显示交流区确认弹窗（合规关键节点）
  showChatConfirm() {
    this.setData({ showChatConfirmModal: true });
  },

  // 关闭交流区确认弹窗
  closeChatConfirm() {
    this.setData({ showChatConfirmModal: false });
  },

  // 确认进入交流区
  confirmEnterChat() {
    this.setData({ showChatConfirmModal: false });
    this.showWechatQr();
  },

  // 显示微信群二维码弹窗
  showWechatQr() {
    const { group } = this.data;
    
    if (group.wecomUrl) {
      // 企业微信，显示合规提示后复制链接
      wx.setClipboardData({
        data: group.wecomUrl,
        success: () => {
          wx.showModal({
            title: '进入交流区',
            content: '链接已复制，请在企业微信中打开。\n\n本群用于家长日常交流，不提供医疗或专业判断。',
            showCancel: false,
            confirmText: '知道了'
          });
        }
      });
    } else if (group.wechatQrUrl) {
      // 显示二维码弹窗
      this.setData({ showQrModal: true });
    } else {
      wx.showToast({ title: '暂无交流区', icon: 'none' });
    }
  },

  // 关闭二维码弹窗
  closeQrModal() {
    this.setData({ showQrModal: false });
  },

  // 预览二维码（长按保存）
  previewQrCode() {
    const { group } = this.data;
    if (group.wechatQrUrl) {
      wx.previewImage({
        urls: [group.wechatQrUrl],
        current: group.wechatQrUrl
      });
    }
  },

  // 保存二维码到相册
  saveQrCode() {
    const { group } = this.data;
    if (!group.wechatQrUrl) return;

    wx.downloadFile({
      url: group.wechatQrUrl,
      success: (res) => {
        wx.saveImageToPhotosAlbum({
          filePath: res.tempFilePath,
          success: () => {
            wx.showToast({ title: '已保存到相册', icon: 'success' });
          },
          fail: () => {
            wx.showToast({ title: '保存失败', icon: 'none' });
          }
        });
      }
    });
  },

  // 跳转到 NodeBB 论坛
  goToNodeBB() {
    const { group } = this.data;
    // TODO: 实现 NodeBB SSO 跳转
    wx.showToast({ title: '即将跳转到讨论区', icon: 'none' });
  },

  // 关闭已退出交流区提示
  closeLeftChatTip() {
    this.setData({ showLeftChatTip: false });
  },

  // 确认退出小组
  confirmLeaveGroup() {
    wx.showModal({
      title: '退出小组',
      content: '确定要退出该小组吗？退出后仍可随时重新加入。',
      confirmText: '确认退出',
      cancelText: '再想想',
      success: async (result) => {
        if (result.confirm) {
          await this.leaveGroup();
        }
      }
    });
  },

  // 退出小组
  async leaveGroup() {
    try {
      await api.leavePeerGroup(this.data.groupId);
      // 显示情绪兜底提示
      wx.showModal({
        title: '已退出小组',
        content: '每个人的节奏不同，退出并不代表失败。\n\n如需帮助，可随时重新加入或咨询专业机构。',
        showCancel: false,
        confirmText: '知道了',
        success: () => {
          wx.navigateBack();
        }
      });
    } catch (error) {
      wx.showToast({ title: error.msg || '退出失败', icon: 'none' });
    }
  },

  // 加入小组
  async joinGroup() {
    try {
      const directJoined = await api.joinPeerGroup({ groupId: this.data.groupId });
      if (directJoined) {
        wx.showToast({ title: '已加入小组', icon: 'success' });
        this.loadGroupDetail();
      } else {
        wx.showToast({ title: '申请已提交，等待审核', icon: 'none' });
      }
    } catch (error) {
      wx.showToast({ title: error.msg || '加入失败', icon: 'none' });
    }
  },

  // 参与话题讨论
  joinTopic() {
    this.setData({
      inputText: '关于今日话题，我想分享的是：'
    });
  },

  // 输入
  onInput(e) {
    this.setData({ inputText: e.detail.value });
  },

  // 发送消息
  sendMessage() {
    if (!this.data.inputText.trim()) {
      wx.showToast({ title: '请输入内容', icon: 'none' });
      return;
    }

    const newMessage = {
      id: Date.now().toString(),
      author: { nickname: '我' },
      content: this.data.inputText,
      timeAgo: '刚刚',
      likeCount: 0,
      isMine: true
    };

    this.setData({
      messages: [...this.data.messages, newMessage],
      inputText: '',
      scrollToView: `msg-${newMessage.id}`
    });
  },

  // 选择图片
  chooseImage() {
    wx.chooseMedia({
      count: 3,
      mediaType: ['image'],
      success: (res) => {
        const images = res.tempFiles.map(f => f.tempFilePath);
        const newMessage = {
          id: Date.now().toString(),
          author: { nickname: '我' },
          content: '分享了图片',
          images: images,
          timeAgo: '刚刚',
          likeCount: 0,
          isMine: true
        };

        this.setData({
          messages: [...this.data.messages, newMessage],
          scrollToView: `msg-${newMessage.id}`
        });
      }
    });
  },

  // 点赞消息
  likeMessage(e) {
    const id = e.currentTarget.dataset.id;
    const messages = this.data.messages.map(msg => {
      if (msg.id === id) {
        return { ...msg, likeCount: (msg.likeCount || 0) + 1 };
      }
      return msg;
    });
    this.setData({ messages });
  },

  // 回复消息
  replyMessage(e) {
    const id = e.currentTarget.dataset.id;
    const message = this.data.messages.find(m => m.id === id);
    this.setData({
      inputText: `回复 @${message.author.nickname}：我也遇到过类似情况，`
    });
  },

  // 预览图片
  previewImage(e) {
    const { url, urls } = e.currentTarget.dataset;
    wx.previewImage({
      current: url,
      urls: urls
    });
  }
});
