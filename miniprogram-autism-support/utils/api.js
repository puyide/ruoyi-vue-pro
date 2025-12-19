/**
 * API 请求封装模块
 * 星语家园 - 统一API管理
 */

// API 配置 - 请根据实际环境修改
const API_CONFIG = {
  // 开发环境
  development: {
    baseUrl: 'http://localhost:48080/app-api',
    timeout: 30000
  },
  // 生产环境
  production: {
    baseUrl: 'https://api.your-domain.com/app-api',
    timeout: 30000
  }
};

// 获取当前环境配置
const getConfig = () => {
  // 可根据实际情况切换环境
  const env = 'development';
  return API_CONFIG[env];
};

const config = getConfig();

/**
 * 封装请求方法
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    };
    
    // 添加认证头
    if (token) {
      header['Authorization'] = `Bearer ${token}`;
    }

    wx.request({
      url: options.url.startsWith('http') ? options.url : `${config.baseUrl}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header,
      timeout: options.timeout || config.timeout,
      success: (res) => {
        // 统一处理响应
        if (res.statusCode === 200) {
          // 业务状态码判断
          if (res.data.code === 0) {
            resolve(res.data.data);
          } else if (res.data.code === 401) {
            // Token 过期，需要重新登录
            handleTokenExpired();
            reject({ code: 401, msg: '登录已过期，请重新登录' });
          } else {
            reject({ code: res.data.code, msg: res.data.msg || '请求失败' });
          }
        } else if (res.statusCode === 401) {
          handleTokenExpired();
          reject({ code: 401, msg: '登录已过期，请重新登录' });
        } else {
          reject({ code: res.statusCode, msg: '网络请求失败' });
        }
      },
      fail: (err) => {
        reject({ code: -1, msg: '网络连接失败，请检查网络' });
      }
    });
  });
};

/**
 * 处理 Token 过期
 */
const handleTokenExpired = () => {
  // 清除登录状态
  wx.removeStorageSync('token');
  wx.removeStorageSync('refreshToken');
  wx.removeStorageSync('userInfo');
  
  const app = getApp();
  if (app) {
    app.globalData.isLogin = false;
    app.globalData.userInfo = null;
  }

  // 显示提示并跳转登录页
  wx.showModal({
    title: '提示',
    content: '登录已过期，请重新登录',
    showCancel: false,
    success: () => {
      wx.navigateTo({ url: '/pages/login/login' });
    }
  });
};

// ==================== 认证相关 API ====================

/**
 * 微信小程序一键登录
 * @param {Object} params - { phoneCode, loginCode, state }
 */
const weixinMiniAppLogin = (params) => {
  return request({
    url: '/member/auth/weixin-mini-app-login',
    method: 'POST',
    data: params
  });
};

/**
 * 手机验证码登录
 * @param {Object} params - { mobile, code, socialType?, socialCode?, socialState? }
 */
const smsLogin = (params) => {
  return request({
    url: '/member/auth/sms-login',
    method: 'POST',
    data: params
  });
};

/**
 * 发送短信验证码
 * @param {Object} params - { mobile, scene }
 * scene: 1-会员登录 2-修改手机 3-重置密码 4-修改密码
 */
const sendSmsCode = (params) => {
  return request({
    url: '/member/auth/send-sms-code',
    method: 'POST',
    data: params
  });
};

/**
 * 手机号+密码登录
 * @param {Object} params - { mobile, password }
 */
const login = (params) => {
  return request({
    url: '/member/auth/login',
    method: 'POST',
    data: params
  });
};

/**
 * 刷新令牌
 * @param {string} refreshToken 
 */
const refreshToken = (refreshToken) => {
  return request({
    url: '/member/auth/refresh-token',
    method: 'POST',
    data: { refreshToken }
  });
};

/**
 * 退出登录
 */
const logout = () => {
  return request({
    url: '/member/auth/logout',
    method: 'POST'
  });
};

// ==================== 用户相关 API ====================

/**
 * 获取当前用户信息
 */
const getUserInfo = () => {
  return request({
    url: '/member/user/get',
    method: 'GET'
  });
};

/**
 * 更新用户昵称
 * @param {string} nickname 
 */
const updateNickname = (nickname) => {
  return request({
    url: '/member/user/update-nickname',
    method: 'PUT',
    data: { nickname }
  });
};

/**
 * 更新用户头像
 * @param {string} avatar 
 */
const updateAvatar = (avatar) => {
  return request({
    url: '/member/user/update-avatar',
    method: 'PUT',
    data: { avatar }
  });
};

/**
 * 绑定手机号
 * @param {Object} params - { mobile, code }
 */
const bindMobile = (params) => {
  return request({
    url: '/member/user/update-mobile',
    method: 'PUT',
    data: params
  });
};

// ==================== 儿童信息相关 API ====================

/**
 * 获取儿童列表
 */
const getChildrenList = () => {
  return request({
    url: '/member/children/list',
    method: 'GET'
  });
};

/**
 * 创建儿童信息
 * @param {Object} params
 */
const createChildren = (params) => {
  return request({
    url: '/member/children/create',
    method: 'POST',
    data: params
  });
};

/**
 * 更新儿童信息
 * @param {Object} params
 */
const updateChildren = (params) => {
  return request({
    url: '/member/children/update',
    method: 'PUT',
    data: params
  });
};

/**
 * 删除儿童信息
 * @param {number} id
 */
const deleteChildren = (id) => {
  return request({
    url: `/member/children/delete?id=${id}`,
    method: 'DELETE'
  });
};

// ==================== 签到相关 API ====================

/**
 * 获取签到配置
 */
const getSignInConfig = () => {
  return request({
    url: '/member/sign-in/config',
    method: 'GET'
  });
};

/**
 * 签到
 */
const signIn = () => {
  return request({
    url: '/member/sign-in/create',
    method: 'POST'
  });
};

/**
 * 获取签到记录
 */
const getSignInRecords = (params) => {
  return request({
    url: '/member/sign-in/record/page',
    method: 'GET',
    data: params
  });
};

// ==================== 同行小组相关 API ====================

/**
 * 获取我加入的小组列表
 */
const getMyPeerGroups = () => {
  return request({
    url: '/member/peer-group/my-groups',
    method: 'GET'
  });
};

/**
 * 获取推荐小组列表
 * @param {Object} params - { category?, limit? }
 */
const getRecommendPeerGroups = (params) => {
  return request({
    url: '/member/peer-group/recommend',
    method: 'GET',
    data: params
  });
};

/**
 * 获取小组列表（按分类）
 * @param {Object} params - { category? }
 */
const getPeerGroupList = (params) => {
  return request({
    url: '/member/peer-group/list',
    method: 'GET',
    data: params
  });
};

/**
 * 获取小组详情
 * @param {number} id 
 */
const getPeerGroupDetail = (id) => {
  return request({
    url: '/member/peer-group/detail',
    method: 'GET',
    data: { id }
  });
};

/**
 * 加入小组
 * @param {Object} params - { groupId, reason? }
 */
const joinPeerGroup = (params) => {
  return request({
    url: '/member/peer-group/join',
    method: 'POST',
    data: params
  });
};

/**
 * 退出小组
 * @param {number} groupId 
 */
const leavePeerGroup = (groupId) => {
  return request({
    url: '/member/peer-group/leave',
    method: 'POST',
    data: { groupId }
  });
};

// ==================== 训练模块相关 API ====================

/**
 * 训练领域枚举
 */
const TrainingDomains = {
  SOCIAL: { code: 1, name: '社交互动', icon: '👥', color: '#FF7A45' },
  LANGUAGE: { code: 2, name: '语言沟通', icon: '💬', color: '#52C41A' },
  EMOTION: { code: 3, name: '情绪管理', icon: '😊', color: '#722ED1' },
  COGNITION: { code: 4, name: '认知学习', icon: '🧠', color: '#1890FF' },
  SENSORY: { code: 5, name: '感觉统合', icon: '✋', color: '#13C2C2' },
  DAILY_LIVING: { code: 6, name: '生活自理', icon: '🏠', color: '#FA8C16' }
};

/**
 * 获取训练领域列表
 */
const getTrainingDomains = () => {
  return request({
    url: '/member/training/template/domains',
    method: 'GET'
  });
};

/**
 * 获取指定领域的训练模板列表
 * @param {number} domain - 领域代码 (1-6)
 */
const getTrainingTemplates = (domain) => {
  return request({
    url: '/member/training/template/list',
    method: 'GET',
    data: { domain }
  });
};

/**
 * 获取训练模板详情
 * @param {number} id - 模板ID
 */
const getTrainingTemplateDetail = (id) => {
  return request({
    url: '/member/training/template/get',
    method: 'GET',
    data: { id }
  });
};

/**
 * 创建训练会话 (触发AI生成训练内容)
 * @param {Object} params - { childId, templateId, difficultyLevel }
 */
const createTrainingSession = (params) => {
  return request({
    url: '/member/training/session/create',
    method: 'POST',
    data: params
  });
};

/**
 * 获取今日训练会话列表
 * @param {number} childId - 孩子ID
 */
const getTodayTrainingSessions = (childId) => {
  return request({
    url: '/member/training/session/today',
    method: 'GET',
    data: { childId }
  });
};

/**
 * 获取训练会话详情
 * @param {number} id - 会话ID
 */
const getTrainingSessionDetail = (id) => {
  return request({
    url: '/member/training/session/get',
    method: 'GET',
    data: { id }
  });
};

/**
 * 获取历史训练会话列表
 * @param {Object} params - { childId, pageNo, pageSize }
 */
const getTrainingSessionHistory = (params) => {
  return request({
    url: '/member/training/session/page',
    method: 'GET',
    data: params
  });
};

/**
 * 提交训练打卡记录
 * @param {Object} params - { sessionId, completed, successCount, totalCount, childMood, durationMinutes, parentComment }
 */
const createTrainingLog = (params) => {
  return request({
    url: '/member/training/log/create',
    method: 'POST',
    data: params
  });
};

/**
 * 获取训练反馈 (AI生成)
 * @param {number} logId - 打卡记录ID
 */
const getTrainingFeedback = (logId) => {
  return request({
    url: '/member/training/log/feedback',
    method: 'GET',
    data: { logId }
  });
};

/**
 * 获取训练统计数据
 * @param {number} childId - 孩子ID
 */
const getTrainingStatistics = (childId) => {
  return request({
    url: '/member/training/log/statistics',
    method: 'GET',
    data: { childId }
  });
};

/**
 * 获取最近7天训练记录
 * @param {number} childId - 孩子ID
 */
const getWeeklyTrainingLogs = (childId) => {
  return request({
    url: '/member/training/log/weekly',
    method: 'GET',
    data: { childId }
  });
};

// ==================== 社区模块相关 API ====================

/**
 * 获取社区分类列表
 */
const getCommunityCategories = () => {
  return request({
    url: '/member/community/categories',
    method: 'GET'
  });
};

/**
 * 获取帖子列表（分页）
 * @param {Object} params - { sort: 'recent'|'popular'|'top', cid?, tag?, keyword?, pageNo, pageSize }
 */
const getCommunityTopics = (params) => {
  return request({
    url: '/member/community/topics',
    method: 'GET',
    data: params
  });
};

/**
 * 获取帖子详情
 * @param {number} tid - 帖子ID
 */
const getCommunityTopicDetail = (tid) => {
  return request({
    url: `/member/community/topics/${tid}`,
    method: 'GET'
  });
};

/**
 * 获取帖子回帖列表（分页）
 * @param {number} tid - 帖子ID
 * @param {Object} params - { pageNo, pageSize }
 */
const getCommunityTopicPosts = (tid, params) => {
  return request({
    url: `/member/community/topics/${tid}/posts`,
    method: 'GET',
    data: params
  });
};

/**
 * 创建帖子
 * @param {Object} params - { title, content, cid, tags?, images?, anonymous? }
 */
const createCommunityTopic = (params) => {
  return request({
    url: '/member/community/topics',
    method: 'POST',
    data: params
  });
};

/**
 * 创建回帖
 * @param {number} tid - 帖子ID
 * @param {Object} params - { content, toPid?, images? }
 */
const createCommunityReply = (tid, params) => {
  return request({
    url: `/member/community/topics/${tid}/reply`,
    method: 'POST',
    data: params
  });
};

/**
 * 点赞帖子/回帖
 * @param {number} pid - 楼层ID
 */
const likeCommunityPost = (pid) => {
  return request({
    url: `/member/community/posts/${pid}/like`,
    method: 'POST'
  });
};

/**
 * 取消点赞
 * @param {number} pid - 楼层ID
 */
const unlikeCommunityPost = (pid) => {
  return request({
    url: `/member/community/posts/${pid}/like`,
    method: 'DELETE'
  });
};

/**
 * 收藏帖子
 * @param {number} tid - 帖子ID
 */
const favoriteCommunityTopic = (tid) => {
  return request({
    url: `/member/community/topics/${tid}/favorite`,
    method: 'POST'
  });
};

/**
 * 取消收藏
 * @param {number} tid - 帖子ID
 */
const unfavoriteCommunityTopic = (tid) => {
  return request({
    url: `/member/community/topics/${tid}/favorite`,
    method: 'DELETE'
  });
};

/**
 * 关注帖子
 * @param {number} tid - 帖子ID
 */
const followCommunityTopic = (tid) => {
  return request({
    url: `/member/community/topics/${tid}/follow`,
    method: 'POST'
  });
};

/**
 * 取消关注帖子
 * @param {number} tid - 帖子ID
 */
const unfollowCommunityTopic = (tid) => {
  return request({
    url: `/member/community/topics/${tid}/follow`,
    method: 'DELETE'
  });
};

/**
 * "我也遇到过"共情互动
 * @param {number} tid - 帖子ID
 */
const relateCommunityTopic = (tid) => {
  return request({
    url: `/member/community/topics/${tid}/relate`,
    method: 'POST'
  });
};

/**
 * 获取我发布的帖子列表
 * @param {Object} params - { pageNo, pageSize }
 */
const getMyCommunityTopics = (params) => {
  return request({
    url: '/member/community/me/topics',
    method: 'GET',
    data: params
  });
};

/**
 * 获取我的回复列表
 * @param {Object} params - { pageNo, pageSize }
 */
const getMyCommunityReplies = (params) => {
  return request({
    url: '/member/community/me/replies',
    method: 'GET',
    data: params
  });
};

/**
 * 获取我收藏的帖子列表
 * @param {Object} params - { pageNo, pageSize }
 */
const getMyCommunityFavorites = (params) => {
  return request({
    url: '/member/community/me/favorites',
    method: 'GET',
    data: params
  });
};

/**
 * 获取未读通知数量
 */
const getCommunityUnreadCount = () => {
  return request({
    url: '/member/community/notifications/unread-count',
    method: 'GET'
  });
};

/**
 * 获取通知列表
 * @param {Object} params - { pageNo, pageSize }
 */
const getCommunityNotifications = (params) => {
  return request({
    url: '/member/community/notifications',
    method: 'GET',
    data: params
  });
};

// ==================== 荣誉系统相关 API ====================

/**
 * 获取荣誉概览（勋章、接力记忆、感谢数量）
 */
const getHonorSummary = () => {
  return request({
    url: '/member/honor/summary',
    method: 'GET'
  });
};

/**
 * 获取用户所有勋章
 */
const getUserBadges = () => {
  return request({
    url: '/member/honor/badges',
    method: 'GET'
  });
};

/**
 * 设置勋章展示状态
 * @param {string} badgeCode - 勋章编码
 * @param {boolean} displayed - 是否展示
 */
const updateBadgeDisplayStatus = (badgeCode, displayed) => {
  return request({
    url: '/member/honor/badge/display',
    method: 'PUT',
    data: { badgeCode, displayed }
  });
};

/**
 * 获取接力记忆列表
 * @param {string} type - 'give'赠出 | 'receive'接收
 */
const getRelayMemories = (type = 'give') => {
  return request({
    url: '/member/honor/relay-memories',
    method: 'GET',
    data: { type }
  });
};

/**
 * 获取感谢私信列表
 */
const getThankMessages = () => {
  return request({
    url: '/member/honor/thank-messages',
    method: 'GET'
  });
};

/**
 * 获取未读感谢数量
 */
const getUnreadThankCount = () => {
  return request({
    url: '/member/honor/thank-messages/unread-count',
    method: 'GET'
  });
};

/**
 * 发送感谢私信
 * @param {Object} params - { toUserId, relayMemoryId, itemId, message }
 */
const sendThankMessage = (params) => {
  return request({
    url: '/member/honor/thank-message/send',
    method: 'POST',
    data: params
  });
};

/**
 * 标记感谢私信为已读
 * @param {number} id - 消息ID
 */
const markThankMessageAsRead = (id) => {
  return request({
    url: `/member/honor/thank-message/${id}/read`,
    method: 'PUT'
  });
};

/**
 * 标记所有感谢私信为已读
 */
const markAllThankMessagesAsRead = () => {
  return request({
    url: '/member/honor/thank-messages/read-all',
    method: 'PUT'
  });
};

// ==================== 导出 ====================

module.exports = {
  // 基础请求
  request,
  config,
  
  // 认证相关
  weixinMiniAppLogin,
  smsLogin,
  sendSmsCode,
  login,
  refreshToken,
  logout,
  
  // 用户相关
  getUserInfo,
  updateNickname,
  updateAvatar,
  bindMobile,
  
  // 儿童信息
  getChildrenList,
  createChildren,
  updateChildren,
  deleteChildren,
  
  // 签到相关
  getSignInConfig,
  signIn,
  getSignInRecords,
  
  // 同行小组
  getMyPeerGroups,
  getRecommendPeerGroups,
  getPeerGroupList,
  getPeerGroupDetail,
  joinPeerGroup,
  leavePeerGroup,
  
  // 训练模块
  TrainingDomains,
  getTrainingDomains,
  getTrainingTemplates,
  getTrainingTemplateDetail,
  createTrainingSession,
  getTodayTrainingSessions,
  getTrainingSessionDetail,
  getTrainingSessionHistory,
  createTrainingLog,
  getTrainingFeedback,
  getTrainingStatistics,
  getWeeklyTrainingLogs,

  // 社区模块
  getCommunityCategories,
  getCommunityTopics,
  getCommunityTopicDetail,
  getCommunityTopicPosts,
  createCommunityTopic,
  createCommunityReply,
  likeCommunityPost,
  unlikeCommunityPost,
  favoriteCommunityTopic,
  unfavoriteCommunityTopic,
  followCommunityTopic,
  unfollowCommunityTopic,
  relateCommunityTopic,
  getMyCommunityTopics,
  getMyCommunityReplies,
  getMyCommunityFavorites,
  getCommunityUnreadCount,
  getCommunityNotifications,

  // 荣誉系统
  getHonorSummary,
  getUserBadges,
  updateBadgeDisplayStatus,
  getRelayMemories,
  getThankMessages,
  getUnreadThankCount,
  sendThankMessage,
  markThankMessageAsRead,
  markAllThankMessagesAsRead
};

