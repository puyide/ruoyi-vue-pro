// pages/post-detail/post-detail.js
const app = getApp();
const api = require('../../utils/api');

Page({
  data: {
    postId: null,
    post: null,
    comments: [],
    relatedPosts: [],
    commentText: '',
    isLoading: true,
    
    // 评论分页
    commentPageNo: 1,
    commentPageSize: 20,
    commentNoMore: false,
    isLoadingComments: false
  },

  onLoad(options) {
    const postId = parseInt(options.id);
    this.setData({ postId });
    this.loadPostDetail();
  },

  // 加载帖子详情
  async loadPostDetail() {
    this.setData({ isLoading: true });
    
    try {
      const post = await api.getCommunityTopicDetail(this.data.postId);
      
      if (post) {
        this.setData({
          post: this.formatPostDetail(post),
          relatedPosts: post.relatedTopics || []
        });
        
        // 加载评论
        this.loadComments();
      } else {
        wx.showToast({
          title: '帖子不存在',
          icon: 'none'
        });
        setTimeout(() => wx.navigateBack(), 1500);
      }
    } catch (err) {
      console.error('加载帖子详情失败:', err);
      wx.showToast({
        title: err.msg || '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ isLoading: false });
    }
  },

  // 格式化帖子详情
  formatPostDetail(post) {
    return {
      id: post.tid,
      tid: post.tid,
      title: post.title,
      author: {
        nickname: post.author?.anonymous ? '匿名家长' : (post.author?.nickname || '用户'),
        avatar: post.author?.avatar || ''
      },
      publishTime: post.createTime || '',
      tags: post.tags || [],
      content: post.content || '',
      images: post.images || [],
      aiAnalysis: post.aiAnalysis,
      viewCount: post.viewCount || 0,
      likeCount: post.likeCount || 0,
      bookmarkCount: post.bookmarkCount || 0,
      postCount: post.postCount || 0,
      liked: post.liked || false,
      bookmarked: post.bookmarked || false,
      following: post.following || false,
      timeAgo: post.timeAgo || ''
    };
  },

  // 加载评论列表
  async loadComments() {
    if (this.data.isLoadingComments || this.data.commentNoMore) return;
    
    this.setData({ isLoadingComments: true });
    
    try {
      const result = await api.getCommunityTopicPosts(this.data.postId, {
        pageNo: this.data.commentPageNo,
        pageSize: this.data.commentPageSize
      });
      
      if (result && result.list) {
        // 过滤掉主楼（第一个帖子是正文）
        const newComments = result.list
          .filter(post => !post.isMainPost)
          .map(post => this.formatComment(post));
        
        const allComments = this.data.commentPageNo === 1 
          ? newComments 
          : [...this.data.comments, ...newComments];
        
        this.setData({
          comments: allComments,
          commentNoMore: newComments.length < this.data.commentPageSize
        });
      }
    } catch (err) {
      console.error('加载评论失败:', err);
    } finally {
      this.setData({ isLoadingComments: false });
    }
  },

  // 格式化评论
  formatComment(post) {
    return {
      id: post.pid,
      pid: post.pid,
      author: {
        nickname: post.author?.anonymous ? '匿名家长' : (post.author?.nickname || '用户'),
        avatar: post.author?.avatar || ''
      },
      content: post.content,
      timeAgo: post.timeAgo || '刚刚',
      likeCount: post.likeCount || 0,
      liked: post.liked || false,
      quotedPost: post.quotedPost
    };
  },

  // 加载更多评论
  loadMoreComments() {
    if (this.data.commentNoMore || this.data.isLoadingComments) return;
    
    this.setData({
      commentPageNo: this.data.commentPageNo + 1
    });
    this.loadComments();
  },

  // 返回
  goBack() {
    wx.navigateBack();
  },

  // 更多操作
  showMore() {
    const post = this.data.post;
    const itemList = [
      post.bookmarked ? '取消收藏' : '收藏',
      post.following ? '取消关注' : '关注',
      '分享',
      '举报'
    ];
    
    wx.showActionSheet({
      itemList,
      success: (res) => {
        this.handleAction(res.tapIndex);
      }
    });
  },

  // 处理操作
  async handleAction(index) {
    const post = this.data.post;
    
    switch(index) {
      case 0: // 收藏/取消收藏
        await this.toggleFavorite();
        break;
      case 1: // 关注/取消关注
        await this.toggleFollow();
        break;
      case 2: // 分享
        // 由微信自动处理
        break;
      case 3: // 举报
        wx.showToast({ title: '举报已提交', icon: 'none' });
        break;
    }
  },

  // 切换收藏状态
  async toggleFavorite() {
    if (!this.checkLogin()) return;
    
    const post = this.data.post;
    
    try {
      if (post.bookmarked) {
        await api.unfavoriteCommunityTopic(post.tid);
      } else {
        await api.favoriteCommunityTopic(post.tid);
      }
      
      this.setData({
        'post.bookmarked': !post.bookmarked,
        'post.bookmarkCount': post.bookmarked 
          ? Math.max(0, post.bookmarkCount - 1) 
          : post.bookmarkCount + 1
      });
      
      wx.showToast({
        title: post.bookmarked ? '已取消收藏' : '已收藏',
        icon: 'success'
      });
    } catch (err) {
      wx.showToast({
        title: err.msg || '操作失败',
        icon: 'none'
      });
    }
  },

  // 切换关注状态
  async toggleFollow() {
    if (!this.checkLogin()) return;
    
    const post = this.data.post;
    
    try {
      if (post.following) {
        await api.unfollowCommunityTopic(post.tid);
      } else {
        await api.followCommunityTopic(post.tid);
      }
      
      this.setData({
        'post.following': !post.following
      });
      
      wx.showToast({
        title: post.following ? '已取消关注' : '已关注',
        icon: 'success'
      });
    } catch (err) {
      wx.showToast({
        title: err.msg || '操作失败',
        icon: 'none'
      });
    }
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      current: url,
      urls: this.data.post.images || [url]
    });
  },

  // 跳转到相关帖子
  goToPost(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/post-detail/post-detail?id=${id}`
    });
  },

  // 评论输入
  onCommentInput(e) {
    this.setData({ commentText: e.detail.value });
  },

  // 提交评论
  async submitComment() {
    if (!this.checkLogin()) return;
    
    const content = this.data.commentText.trim();
    if (!content) {
      wx.showToast({ title: '请输入评论内容', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '发送中...' });
    
    try {
      const pid = await api.createCommunityReply(this.data.postId, {
        content: content
      });
      
      // 添加到评论列表
      const newComment = {
        id: pid,
        pid: pid,
        author: {
          nickname: app.globalData.userInfo?.nickname || '我',
          avatar: app.globalData.userInfo?.avatar || ''
        },
        content: content,
        timeAgo: '刚刚',
        likeCount: 0,
        liked: false
      };

      this.setData({
        comments: [newComment, ...this.data.comments],
        commentText: '',
        'post.postCount': (this.data.post.postCount || 0) + 1
      });

      wx.showToast({ title: '评论成功', icon: 'success' });
    } catch (err) {
      wx.showToast({
        title: err.msg || '评论失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 点赞评论
  async likeComment(e) {
    if (!this.checkLogin()) return;
    
    const pid = e.currentTarget.dataset.id;
    const comment = this.data.comments.find(c => c.id === pid || c.pid === pid);
    
    if (!comment) return;
    
    try {
      if (comment.liked) {
        await api.unlikeCommunityPost(pid);
      } else {
        await api.likeCommunityPost(pid);
      }
      
      const comments = this.data.comments.map(c => {
        if (c.id === pid || c.pid === pid) {
          return {
            ...c,
            liked: !c.liked,
            likeCount: c.liked ? Math.max(0, c.likeCount - 1) : c.likeCount + 1
          };
        }
        return c;
      });
      
      this.setData({ comments });
    } catch (err) {
      wx.showToast({
        title: err.msg || '操作失败',
        icon: 'none'
      });
    }
  },

  // 回复评论
  replyComment(e) {
    const id = e.currentTarget.dataset.id;
    const comment = this.data.comments.find(c => c.id === id || c.pid === id);
    if (comment) {
      this.setData({
        commentText: `回复 @${comment.author.nickname}: `
      });
    }
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

  // 分享
  onShareAppMessage() {
    return {
      title: this.data.post?.title || '分享帖子',
      path: `/pages/post-detail/post-detail?id=${this.data.postId}`
    };
  }
});
