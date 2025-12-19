// pages/search/search.js
Page({
  data: {
    keyword: '',
    searchHistory: [],
    hotKeywords: ['眼神训练', '语言发展', '情绪管理', '入园准备', '感统训练', '呼名反应'],
    hasSearched: false,
    resultTabs: [
      { id: 'all', name: '全部', count: 0 },
      { id: 'post', name: '经验帖', count: 0 },
      { id: 'institution', name: '机构', count: 0 }
    ],
    resultTab: 'all',
    searchResults: []
  },

  onLoad() {
    const history = wx.getStorageSync('searchHistory') || [];
    this.setData({ searchHistory: history.slice(0, 10) });
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  clearKeyword() {
    this.setData({ keyword: '', hasSearched: false, searchResults: [] });
  },

  doSearch() {
    if (!this.data.keyword.trim()) return;
    
    this.saveHistory(this.data.keyword);
    this.performSearch();
  },

  searchFromHistory(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({ keyword });
    this.saveHistory(keyword);
    this.performSearch();
  },

  saveHistory(keyword) {
    let history = [...this.data.searchHistory];
    const index = history.indexOf(keyword);
    if (index > -1) history.splice(index, 1);
    history.unshift(keyword);
    history = history.slice(0, 10);
    this.setData({ searchHistory: history });
    wx.setStorageSync('searchHistory', history);
  },

  clearHistory() {
    wx.showModal({
      title: '清空搜索历史',
      content: '确定清空所有搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({ searchHistory: [] });
          wx.removeStorageSync('searchHistory');
        }
      }
    });
  },

  performSearch() {
    // 模拟搜索结果
    const mockResults = [
      { id: '1', type: 'post', title: `${this.data.keyword}相关经验分享`, description: '家长分享的实用经验...' },
      { id: '2', type: 'institution', title: `${this.data.keyword}康复中心`, description: '专业康复训练机构' },
      { id: '3', type: 'post', title: `如何进行${this.data.keyword}`, description: '详细的训练方法和步骤...' }
    ];

    this.setData({
      hasSearched: true,
      searchResults: mockResults,
      resultTabs: [
        { id: 'all', name: '全部', count: mockResults.length },
        { id: 'post', name: '经验帖', count: mockResults.filter(r => r.type === 'post').length },
        { id: 'institution', name: '机构', count: mockResults.filter(r => r.type === 'institution').length }
      ]
    });
  },

  switchResultTab(e) {
    this.setData({ resultTab: e.currentTarget.dataset.id });
  },

  goToResult(e) {
    const item = e.currentTarget.dataset.item;
    if (item.type === 'post') {
      wx.navigateTo({ url: `/pages/post-detail/post-detail?id=${item.id}` });
    } else if (item.type === 'institution') {
      wx.navigateTo({ url: `/pages/institution-detail/institution-detail?id=${item.id}` });
    }
  },

  goBack() {
    wx.navigateBack();
  }
});

