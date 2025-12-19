// pages/group/group.js
const app = getApp();
const api = require('../../utils/api');

Page({
  data: {
    loading: true,
    myGroups: [],
    recommendGroups: [],
    allGroups: [],
    categories: [
      { id: 'all', name: '全部' },
      { id: 'language', name: '语言' },
      { id: 'emotion', name: '情绪' },
      { id: 'sensory', name: '感统' },
      { id: 'social', name: '社交' },
      { id: 'kindergarten', name: '入园' }
    ],
    currentCategory: 'all'
  },

  onLoad() {
    this.loadData();
  },

  onShow() {
    // 每次显示时刷新数据，确保加入/退出后数据更新
    this.loadData();
  },

  onPullDownRefresh() {
    this.loadData().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 加载数据
  async loadData() {
    this.setData({ loading: true });

    try {
      // 并行请求三个接口
      const [myGroups, recommendGroups, allGroups] = await Promise.all([
        this.loadMyGroups(),
        this.loadRecommendGroups(),
        this.loadAllGroups(this.data.currentCategory)
      ]);

      this.setData({
        myGroups,
        recommendGroups,
        allGroups,
        loading: false
      });
    } catch (error) {
      console.error('加载小组数据失败:', error);
      this.setData({ loading: false });
      
      // 如果未登录，使用 Mock 数据
      if (error.code === 401) {
        this.loadMockData();
      } else {
        wx.showToast({ title: error.msg || '加载失败', icon: 'none' });
      }
    }
  },

  // 加载我加入的小组
  async loadMyGroups() {
    try {
      const data = await api.getMyPeerGroups();
      return data || [];
    } catch (e) {
      console.error('loadMyGroups error:', e);
      return [];
    }
  },

  // 加载推荐小组
  async loadRecommendGroups() {
    try {
      const data = await api.getRecommendPeerGroups({ limit: 5 });
      return data || [];
    } catch (e) {
      console.error('loadRecommendGroups error:', e);
      return [];
    }
  },

  // 加载全部小组
  async loadAllGroups(category) {
    try {
      const params = {};
      if (category && category !== 'all') {
        params.category = category;
      }
      const data = await api.getPeerGroupList(params);
      return data || [];
    } catch (e) {
      console.error('loadAllGroups error:', e);
      return [];
    }
  },

  // Mock 数据（未登录时使用）
  loadMockData() {
    this.setData({
      myGroups: [
        {
          id: 1,
          name: '3-6岁 · 语言发展',
          icon: '💬',
          theme: 'orange',
          tags: ['语言', '3-6岁'],
          memberCount: 101,
          lastActivity: '刚刚有新分享'
        },
        {
          id: 2,
          name: '入园准备 · 情绪稳定',
          icon: '🏫',
          theme: 'purple',
          tags: ['入园', '情绪'],
          memberCount: 78,
          lastActivity: '今日有新话题'
        }
      ],
      recommendGroups: [
        {
          id: 3,
          name: '眼神训练互助组',
          icon: '👀',
          theme: 'green',
          tags: ['眼神', '社交'],
          memberCount: 56,
          description: '专注眼神接触训练的家长互助小组，每日分享训练心得',
          joined: false
        },
        {
          id: 4,
          name: '感统训练分享圈',
          icon: '🎯',
          theme: 'blue',
          tags: ['感统', '居家'],
          memberCount: 89,
          description: '感觉统合训练经验分享，在家也能做的感统游戏',
          joined: false
        }
      ],
      allGroups: [
        {
          id: 5,
          name: '0-3岁早期干预',
          icon: '👶',
          theme: 'yellow',
          tags: ['0-3岁', '早期干预'],
          memberCount: 134,
          joined: false
        },
        {
          id: 6,
          name: '情绪管理研讨',
          icon: '😊',
          theme: 'purple',
          tags: ['情绪', '行为'],
          memberCount: 67,
          joined: false
        },
        {
          id: 7,
          name: '社交技能训练',
          icon: '🤝',
          theme: 'green',
          tags: ['社交', '互动'],
          memberCount: 92,
          joined: false
        }
      ]
    });
  },

  // 进入小组
  enterGroup(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/group-detail/group-detail?id=${id}`
    });
  },

  // 加入/退出小组
  async toggleJoin(e) {
    const id = e.currentTarget.dataset.id;
    
    // 查找小组信息
    let group = [...this.data.recommendGroups, ...this.data.allGroups].find(g => g.id === id);
    if (!group) return;

    const isJoined = group.joined;

    if (isJoined) {
      // 退出小组 - 显示情绪兜底确认
      wx.showModal({
        title: '退出小组',
        content: '确定要退出该小组吗？退出后仍可随时重新加入。',
        confirmText: '确认退出',
        cancelText: '再想想',
        success: async (result) => {
          if (result.confirm) {
            await this.doLeaveGroup(id);
          }
        }
      });
    } else {
      // 加入小组 - 直接加入（小组说明弹窗在进入交流区时显示）
      await this.doJoinGroup(id);
    }
  },

  // 执行退出小组
  async doLeaveGroup(id) {
    try {
      await api.leavePeerGroup(id);
      // 情绪兜底提示
      wx.showModal({
        title: '已退出小组',
        content: '每个人的节奏不同，退出并不代表失败。如需帮助，可随时重新加入。',
        showCancel: false,
        confirmText: '知道了'
      });
      this.loadData();
    } catch (error) {
      console.error('doLeaveGroup error:', error);
      wx.showToast({ title: error.msg || '退出失败', icon: 'none' });
      
      if (error.code === 401) {
        this.mockToggleJoin(id, true);
      }
    }
  },

  // 执行加入小组
  async doJoinGroup(id) {
    try {
      const directJoined = await api.joinPeerGroup({ groupId: id });
      if (directJoined) {
        wx.showToast({ title: '已加入小组', icon: 'success' });
      } else {
        wx.showToast({ title: '申请已提交，等待审核', icon: 'none' });
      }
      this.loadData();
    } catch (error) {
      console.error('doJoinGroup error:', error);
      wx.showToast({ title: error.msg || '加入失败', icon: 'none' });
      
      if (error.code === 401) {
        this.mockToggleJoin(id, false);
      }
    }
  },

  // 模拟加入/退出（未登录时）
  mockToggleJoin(id, isJoined) {
    // 更新推荐小组
    const recommendGroups = this.data.recommendGroups.map(group => {
      if (group.id === id) {
        return { ...group, joined: !group.joined };
      }
      return group;
    });

    // 更新全部小组
    const allGroups = this.data.allGroups.map(group => {
      if (group.id === id) {
        return { ...group, joined: !group.joined };
      }
      return group;
    });

    this.setData({ recommendGroups, allGroups });

    if (!isJoined) {
      wx.showToast({ title: '已加入小组', icon: 'success' });
    } else {
      wx.showToast({ title: '已退出小组', icon: 'none' });
    }
  },

  // 切换分类
  async switchCategory(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ currentCategory: id });

    try {
      const allGroups = await this.loadAllGroups(id);
      this.setData({ allGroups });
    } catch (error) {
      console.error('switchCategory error:', error);
    }
  }
});
