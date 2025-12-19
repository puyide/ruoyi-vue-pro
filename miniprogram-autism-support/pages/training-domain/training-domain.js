// pages/training-domain/training-domain.js
const api = require('../../utils/api');

Page({
  data: {
    domains: [],
    loading: true
  },

  onLoad() {
    this.loadDomains();
  },

  async loadDomains() {
    this.setData({ loading: true });
    
    try {
      const domains = await api.getTrainingDomains();
      this.setData({ domains });
    } catch (err) {
      console.error('加载训练领域失败:', err);
      // 使用本地定义的领域数据
      const domains = Object.values(api.TrainingDomains).map(d => ({
        code: d.code,
        name: d.name,
        icon: d.icon,
        color: d.color,
        description: this.getDomainDescription(d.code)
      }));
      this.setData({ domains });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 获取领域描述
  getDomainDescription(code) {
    const descriptions = {
      1: '眼神交流、共同注意、轮流互动、分享行为等社交技能训练',
      2: '发音练习、词汇积累、表达需求、对话交流等语言能力训练',
      3: '情绪识别、情绪表达、情绪调节、同理心培养等训练',
      4: '注意力训练、记忆力练习、逻辑思维、问题解决等认知训练',
      5: '触觉脱敏、前庭觉训练、本体觉训练、感觉整合游戏',
      6: '穿衣、洗漱、进食、如厕等日常生活技能训练'
    };
    return descriptions[code] || '';
  },

  // 返回
  goBack() {
    wx.navigateBack();
  },

  // 选择领域
  selectDomain(e) {
    const { code, name } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/training-template/training-template?domain=${code}&name=${encodeURIComponent(name)}`
    });
  }
});

