// pages/community/community.js
const app = getApp();
const api = require('../../utils/api');

Page({
  data: {
    // 分类标签
    tabs: [
      { id: 'all', name: '全部', cid: null }
    ],
    currentTab: 0,
    
    // 排序方式
    sortOptions: [
      { id: 'recent', name: '最新' },
      { id: 'popular', name: '热门' },
      { id: 'top', name: '精华' }
    ],
    currentSort: 'recent',
    
    // 帖子列表
    posts: [],
    pageNo: 1,
    pageSize: 10,
    isLoading: false,
    isRefreshing: false,
    noMore: false,
    
    // 通知红点
    unreadCount: 0
  },

  onLoad() {
    this.loadCategories();
    this.loadPosts();
    this.loadUnreadCount();
  },

  onShow() {
    // 检查是否有新发布的帖子
    const newPost = wx.getStorageSync('newPost');
    if (newPost) {
      wx.removeStorageSync('newPost');
      this.onRefresh();
    }
  },

  // 加载分类列表
  async loadCategories() {
    try {
      const categories = await api.getCommunityCategories();
      if (categories && categories.length > 0) {
        const tabs = [{ id: 'all', name: '全部', cid: null }];
        categories.forEach(cat => {
          tabs.push({
            id: cat.cid.toString(),
            name: cat.name,
            cid: cat.cid,
            icon: cat.icon
          });
        });
        this.setData({ tabs });
      }
    } catch (err) {
      console.error('加载分类失败:', err);
    }
  },

  // 加载未读通知数
  async loadUnreadCount() {
    if (!app.globalData.isLogin) return;
    
    try {
      const count = await api.getCommunityUnreadCount();
      this.setData({ unreadCount: count || 0 });
    } catch (err) {
      console.error('加载未读数失败:', err);
    }
  },

  // 切换标签
  switchTab(e) {
    const index = e.currentTarget.dataset.index;
    if (index === this.data.currentTab) return;
    
    this.setData({
      currentTab: index,
      posts: [],
      pageNo: 1,
      noMore: false
    });
    this.loadPosts();
  },

  // 切换排序
  switchSort(e) {
    const sort = e.currentTarget.dataset.sort;
    if (sort === this.data.currentSort) return;
    
    this.setData({
      currentSort: sort,
      posts: [],
      pageNo: 1,
      noMore: false
    });
    this.loadPosts();
  },

  // 加载帖子列表
  async loadPosts() {
    if (this.data.isLoading || this.data.noMore) return;
    
    this.setData({ isLoading: true });

    try {
      const currentCat = this.data.tabs[this.data.currentTab];
      const params = {
        sort: this.data.currentSort,
        pageNo: this.data.pageNo,
        pageSize: this.data.pageSize
      };
      
      // 如果选择了特定分类
      if (currentCat && currentCat.cid) {
        params.cid = currentCat.cid;
      }

      const result = await api.getCommunityTopics(params);
      
      if (result && result.list) {
        const newPosts = result.list.map(post => this.formatPost(post));
        const allPosts = this.data.pageNo === 1 ? newPosts : [...this.data.posts, ...newPosts];
        
        this.setData({
          posts: allPosts,
          noMore: newPosts.length < this.data.pageSize
        });
      } else {
        if (this.data.pageNo === 1) {
          this.setData({ posts: [], noMore: true });
        }
      }
    } catch (err) {
      console.error('加载帖子失败:', err);
      wx.showToast({
        title: err.msg || '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({
        isLoading: false,
        isRefreshing: false
      });
    }
  },

  // 格式化帖子数据
  formatPost(post) {
    return {
      id: post.tid,
      tid: post.tid,
      title: post.title,
      tags: post.tags || [],
      aiSummary: post.aiSummary || post.excerpt,
      author: {
        nickname: post.author?.anonymous ? '匿名家长' : (post.author?.nickname || '用户'),
        avatar: post.author?.avatar || ''
      },
      relateCount: post.relateCount || post.bookmarkCount || 0,
      commentCount: post.postCount || 0,
      viewCount: post.viewCount || 0,
      likeCount: post.likeCount || 0,
      timeAgo: post.timeAgo || '刚刚',
      liked: post.liked || false,
      bookmarked: post.bookmarked || false
    };
  },

  // 下拉刷新
  onRefresh() {
    this.setData({
      isRefreshing: true,
      pageNo: 1,
      noMore: false,
      posts: []
    });
    this.loadPosts();
    this.loadUnreadCount();
  },

  // 加载更多
  loadMore() {
    if (this.data.noMore || this.data.isLoading) return;
    
    this.setData({
      pageNo: this.data.pageNo + 1
    });
    this.loadPosts();
  },

  // 展开摘要
  expandSummary(e) {
    const id = e.currentTarget.dataset.id;
    // 跳转到详情页查看完整内容
    this.goToPostDetail({ currentTarget: { dataset: { id } } });
  },

  // "我也遇到过"
  async onRelate(e) {
    if (!this.checkLogin()) return;
    
    const id = e.currentTarget.dataset.id;
    
    try {
      await api.relateCommunityTopic(id);
      
      // 更新本地状态
      const posts = this.data.posts.map(post => {
        if (post.id === id || post.tid === id) {
          return {
            ...post,
            relateCount: (post.relateCount || 0) + 1
          };
        }
        return post;
      });
      this.setData({ posts });
      
      wx.showToast({
        title: '已标记',
        icon: 'success'
      });
    } catch (err) {
      wx.showToast({
        title: err.msg || '操作失败',
        icon: 'none'
      });
    }
  },

  // 跳转到帖子详情
  goToPostDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/post-detail/post-detail?id=${id}`
    });
  },

  // 跳转到发布页
  goToCreate() {
    if (!this.checkLogin()) return;
    
    wx.navigateTo({
      url: '/pages/post-create/post-create'
    });
  },

  // 跳转到通知页
  goToNotifications() {
    if (!this.checkLogin()) return;
    
    wx.navigateTo({
      url: '/pages/notifications/notifications'
    });
  },

  // 检查登录状态
  checkLogin() {
    if (!app.globalData.isLogin) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再操作',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/login/login' });
          }
        }
      });
      return false;
    }
    return true;
  },

  // 页面分享
  onShareAppMessage() {
    return {
      title: '星语家园 - 一起成长，共同前行',
      path: '/pages/community/community'
    };
  }
});
