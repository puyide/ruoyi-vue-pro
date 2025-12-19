/**
 * 勋章触发器（确认型，不是成就型）
 * 
 * 设计理念：只在"第一次"触发，不计数，不比较
 * 
 * 星语家园 - 荣誉系统
 */

const api = require('./api');

/**
 * 事件类型枚举
 */
const EventType = {
  // 训练相关
  TRAINING_FIRST_COMPLETE: 'training_first_complete',
  TRAINING_COMPLETE: 'training_complete',
  TRAINING_RESUME_AFTER_BREAK: 'training_resume_after_break',
  TRAINING_WEEK_COMPLETE: 'training_week_complete',
  
  // 帖子相关
  POST_FIRST_PUBLISH: 'post_first_publish',
  POST_FIRST_COMMENT_RECEIVED: 'post_first_comment_received',
  POST_FIRST_COLLECTED: 'post_first_collected',
  
  // 接力相关
  RELAY_ITEM_GIVE: 'relay_item_give',
  RELAY_ITEM_MATCHED: 'relay_item_matched',
  RELAY_CHAIN_EXTENDED: 'relay_chain_extended',
  THANK_RECEIVED: 'thank_received',
  
  // 小组相关
  GROUP_FIRST_JOIN: 'group_first_join',
  
  // 其他
  APP_VISIT: 'app_visit',
  APP_VISIT_7_DAYS: 'app_visit_7_days',
  FIRST_SHARE: 'first_share'
};

/**
 * 事件对应的勋章信息（用于本地展示，真正的触发由后端处理）
 */
const EventBadgeMap = {
  [EventType.TRAINING_FIRST_COMPLETE]: {
    code: 'first_step',
    name: '第一步',
    description: '迈出第一步，就已经很了不起',
    icon: '👣'
  },
  [EventType.TRAINING_RESUME_AFTER_BREAK]: {
    code: 'slow_is_ok',
    name: '慢慢走也没关系',
    description: '中断过也没关系，你回来了',
    icon: '🐢'
  },
  [EventType.TRAINING_COMPLETE]: {
    code: 'today_with_you',
    name: '今天也陪着',
    description: '今天，你陪在孩子身边',
    icon: '🤝'
  },
  [EventType.RELAY_ITEM_MATCHED]: {
    code: 'someone_caught_it',
    name: '有人接住了',
    description: '你传递的东西，有人接住了',
    icon: '🎁'
  },
  [EventType.RELAY_CHAIN_EXTENDED]: {
    code: 'warmth_flows',
    name: '温暖没有停',
    description: '温暖在流动，没有停在你这里',
    icon: '🔄'
  },
  [EventType.THANK_RECEIVED]: {
    code: 'been_thanked',
    name: '被感谢过',
    description: '有人想对你说谢谢',
    icon: '💌'
  },
  [EventType.GROUP_FIRST_JOIN]: {
    code: 'not_alone',
    name: '同行者',
    description: '你不是一个人在走这条路',
    icon: '👥'
  },
  [EventType.POST_FIRST_COMMENT_RECEIVED]: {
    code: 'someone_sees_you',
    name: '有人懂你',
    description: '有人认真看过你写的',
    icon: '👀'
  },
  [EventType.POST_FIRST_PUBLISH]: {
    code: 'left_a_mark',
    name: '留下痕迹',
    description: '你的故事，被记录下来了',
    icon: '✍️'
  },
  [EventType.POST_FIRST_COLLECTED]: {
    code: 'someone_kept_it',
    name: '有人收藏',
    description: '有人把你的话放进了口袋',
    icon: '💝'
  },
  [EventType.APP_VISIT_7_DAYS]: {
    code: 'quiet_companion',
    name: '安静陪伴',
    description: '你一直都在',
    icon: '🌙'
  }
};

/**
 * 本地存储已获得的勋章（用于避免重复展示）
 */
const STORAGE_KEY = 'user_badges_shown';

/**
 * 获取已展示过的勋章
 */
const getShownBadges = () => {
  try {
    return wx.getStorageSync(STORAGE_KEY) || [];
  } catch (e) {
    return [];
  }
};

/**
 * 记录已展示的勋章
 */
const markBadgeShown = (badgeCode) => {
  try {
    const shown = getShownBadges();
    if (!shown.includes(badgeCode)) {
      shown.push(badgeCode);
      wx.setStorageSync(STORAGE_KEY, shown);
    }
  } catch (e) {
    console.error('记录勋章展示失败:', e);
  }
};

/**
 * 检查勋章是否已展示过
 */
const hasBadgeShown = (badgeCode) => {
  return getShownBadges().includes(badgeCode);
};

/**
 * 显示勋章获得弹窗（温暖的确认，不是炫耀的成就）
 */
const showBadgeUnlockModal = (badge) => {
  if (!badge || hasBadgeShown(badge.code)) {
    return;
  }

  // 标记为已展示
  markBadgeShown(badge.code);

  // 显示温暖的确认弹窗
  wx.showModal({
    title: badge.icon + ' ' + badge.name,
    content: badge.description,
    showCancel: false,
    confirmText: '我知道了',
    confirmColor: '#FF7A45'
  });
};

/**
 * 处理后端返回的新勋章
 * @param {Object} badgeData - 后端返回的勋章数据
 */
const handleNewBadge = (badgeData) => {
  if (!badgeData) {
    return;
  }

  showBadgeUnlockModal({
    code: badgeData.code,
    name: badgeData.name,
    description: badgeData.description,
    icon: badgeData.icon
  });
};

/**
 * 本地模拟触发勋章检查（用于开发测试）
 * 真实环境中，勋章触发由后端处理
 */
const localTriggerCheck = (eventType) => {
  const badge = EventBadgeMap[eventType];
  if (badge && !hasBadgeShown(badge.code)) {
    // 这里可以添加额外的本地条件检查
    return badge;
  }
  return null;
};

/**
 * 格式化相对时间（温暖的表达）
 */
const formatRelativeTime = (time) => {
  if (!time) return '';
  
  const date = new Date(time);
  const now = new Date();
  const diffMs = now - date;
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  
  if (diffDays === 0) {
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    if (diffHours === 0) {
      return '刚刚';
    }
    return diffHours + '小时前';
  } else if (diffDays === 1) {
    return '昨天';
  } else if (diffDays < 7) {
    return diffDays + '天前';
  } else if (diffDays < 30) {
    return Math.floor(diffDays / 7) + '周前';
  } else if (diffDays < 365) {
    return Math.floor(diffDays / 30) + '个月前';
  } else {
    return Math.floor(diffDays / 365) + '年前';
  }
};

/**
 * 生成温暖的问候语
 */
const generateGreeting = () => {
  const hour = new Date().getHours();
  if (hour < 6) {
    return '夜深了，辛苦了';
  } else if (hour < 12) {
    return '早上好，新的一天';
  } else if (hour < 18) {
    return '下午好，今天怎么样？';
  } else {
    return '晚上好，今天也辛苦了';
  }
};

module.exports = {
  EventType,
  EventBadgeMap,
  showBadgeUnlockModal,
  handleNewBadge,
  localTriggerCheck,
  hasBadgeShown,
  formatRelativeTime,
  generateGreeting
};

