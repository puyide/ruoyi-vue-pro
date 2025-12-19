// pages/training-template/training-template.js
const api = require('../../utils/api');

Page({
  data: {
    domain: 0,
    domainName: '',
    domainInfo: null,
    templates: [],
    loading: true,
    
    // 选择难度弹窗
    showLevelPicker: false,
    selectedTemplate: null,
    selectedLevel: 1,
    
    // 儿童信息
    children: [],
    currentChild: null
  },

  onLoad(options) {
    const domain = parseInt(options.domain) || 1;
    const domainName = decodeURIComponent(options.name || '');
    
    this.setData({ domain, domainName });
    this.loadDomainInfo();
    this.loadData();
  },

  loadDomainInfo() {
    const domainColors = {
      1: '#FF7A45',
      2: '#52C41A', 
      3: '#722ED1',
      4: '#1890FF',
      5: '#13C2C2',
      6: '#FA8C16'
    };
    
    const domainIcons = {
      1: '👥', 2: '💬', 3: '😊', 4: '🧠', 5: '✋', 6: '🏠'
    };

    this.setData({
      domainInfo: {
        color: domainColors[this.data.domain] || '#FF7A45',
        icon: domainIcons[this.data.domain] || '📝'
      }
    });
  },

  async loadData() {
    this.setData({ loading: true });
    
    try {
      await Promise.all([
        this.loadChildren(),
        this.loadTemplates()
      ]);
    } catch (err) {
      console.error('加载数据失败:', err);
      wx.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  async loadChildren() {
    try {
      const children = await api.getChildrenList();
      if (children && children.length > 0) {
        this.setData({ 
          children,
          currentChild: children[0]
        });
      } else {
        this.setData({
          children: [],
          currentChild: { id: 0, nickname: '我的宝贝', age: 4 }
        });
      }
    } catch (err) {
      this.setData({
        children: [],
        currentChild: { id: 0, nickname: '我的宝贝', age: 4 }
      });
    }
  },

  async loadTemplates() {
    try {
      const templates = await api.getTrainingTemplates(this.data.domain);
      this.setData({ templates: templates || [] });
    } catch (err) {
      console.error('加载模板失败:', err);
      // 使用模拟数据
      this.setData({ templates: this.getMockTemplates() });
    }
  },

  getMockTemplates() {
    const mockData = {
      1: [ // 社交互动
        { id: 1, name: '眼神跟随训练', briefGoal: '提升眼神接触能力', recommendedFrequency: '每天1-2次', icon: '👀' },
        { id: 2, name: '共同注意训练', briefGoal: '培养共同关注能力', recommendedFrequency: '每天1次', icon: '👆' },
        { id: 3, name: '轮流游戏训练', briefGoal: '学习轮流等待', recommendedFrequency: '每天1次', icon: '🎲' }
      ],
      2: [ // 语言沟通
        { id: 4, name: '呼名反应训练', briefGoal: '提高呼名应答率', recommendedFrequency: '每天2-3次', icon: '👂' },
        { id: 5, name: '模仿发声训练', briefGoal: '增加语音模仿能力', recommendedFrequency: '每天1次', icon: '🗣️' },
        { id: 6, name: '需求表达训练', briefGoal: '学习表达基本需求', recommendedFrequency: '随时', icon: '🙋' }
      ],
      3: [ // 情绪管理
        { id: 7, name: '情绪识别训练', briefGoal: '识别基本情绪', recommendedFrequency: '每天1次', icon: '😊' },
        { id: 8, name: '情绪调节训练', briefGoal: '学习调节情绪', recommendedFrequency: '需要时', icon: '🧘' },
        { id: 9, name: '社交情境训练', briefGoal: '理解社交情境', recommendedFrequency: '每周2-3次', icon: '🎭' }
      ],
      4: [ // 认知学习
        { id: 10, name: '颜色配对训练', briefGoal: '认识和配对颜色', recommendedFrequency: '每天1次', icon: '🎨' },
        { id: 11, name: '形状认知训练', briefGoal: '识别基本形状', recommendedFrequency: '每天1次', icon: '🔷' },
        { id: 12, name: '分类排序训练', briefGoal: '学习分类和排序', recommendedFrequency: '每天1次', icon: '📦' }
      ],
      5: [ // 感觉统合
        { id: 13, name: '触觉脱敏训练', briefGoal: '降低触觉敏感', recommendedFrequency: '每天1次', icon: '✋' },
        { id: 14, name: '前庭觉训练', briefGoal: '改善平衡能力', recommendedFrequency: '每天1次', icon: '🎢' },
        { id: 15, name: '本体觉训练', briefGoal: '增强身体意识', recommendedFrequency: '每天1次', icon: '🏃' }
      ],
      6: [ // 生活自理
        { id: 16, name: '穿衣训练', briefGoal: '学习独立穿衣', recommendedFrequency: '每天2次', icon: '👕' },
        { id: 17, name: '洗手训练', briefGoal: '掌握正确洗手', recommendedFrequency: '每次饭前', icon: '🧼' },
        { id: 18, name: '进食训练', briefGoal: '提升独立进食能力', recommendedFrequency: '每餐', icon: '🍽️' }
      ]
    };
    
    return mockData[this.data.domain] || [];
  },

  // 返回
  goBack() {
    wx.navigateBack();
  },

  // 点击模板
  selectTemplate(e) {
    const { id } = e.currentTarget.dataset;
    const template = this.data.templates.find(t => t.id === id);
    
    if (template) {
      this.setData({
        selectedTemplate: template,
        selectedLevel: 1,
        showLevelPicker: true
      });
    }
  },

  // 选择难度等级
  selectLevel(e) {
    const level = parseInt(e.currentTarget.dataset.level);
    this.setData({ selectedLevel: level });
  },

  // 关闭难度选择弹窗
  closeLevelPicker() {
    this.setData({
      showLevelPicker: false,
      selectedTemplate: null
    });
  },

  // 确认开始训练
  async confirmStart() {
    const { selectedTemplate, selectedLevel, currentChild } = this.data;
    
    if (!selectedTemplate) {
      wx.showToast({ title: '请选择训练模板', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '正在生成训练...' });

    try {
      // 创建训练会话
      const session = await api.createTrainingSession({
        childId: currentChild?.id || 0,
        templateId: selectedTemplate.id,
        difficultyLevel: selectedLevel
      });

      wx.hideLoading();
      this.closeLevelPicker();

      // 跳转到训练详情页
      wx.navigateTo({
        url: `/pages/training-detail/training-detail?sessionId=${session.id}`
      });
    } catch (err) {
      wx.hideLoading();
      console.error('创建训练会话失败:', err);
      
      // 模拟模式：直接跳转
      this.closeLevelPicker();
      wx.navigateTo({
        url: `/pages/training-detail/training-detail?templateId=${selectedTemplate.id}&level=${selectedLevel}`
      });
    }
  },

  // 查看模板详情
  viewTemplateDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/training-detail/training-detail?templateId=${id}`
    });
  }
});

