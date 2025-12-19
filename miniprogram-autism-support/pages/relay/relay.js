// pages/relay/relay.js
const app = getApp();

Page({
  data: {
    items: [],
    page: 1,
    pageSize: 10,
    isLoading: false,
    isRefreshing: false,
    noMore: false,
    
    currentFilter: {
      age: 'all',
      type: '',
      typeLabel: '',
      method: '',
      methodLabel: ''
    },
    
    ageFilters: [
      { value: 'all', label: '全部' },
      { value: '0-3', label: '0-3岁' },
      { value: '3-6', label: '3-6岁' },
      { value: '6-12', label: '6-12岁' }
    ],
    
    typeFilters: [
      { value: '', label: '全部' },
      { value: 'language', label: '语言训练' },
      { value: 'sensory', label: '感统训练' },
      { value: 'attention', label: '注意力' },
      { value: 'social', label: '社交' },
      { value: 'emotion', label: '情绪管理' },
      { value: 'other', label: '其他' }
    ],
    
    methodFilters: [
      { value: '', label: '全部' },
      { value: 'give', label: '赠送' },
      { value: 'borrow', label: '借用' },
      { value: 'sell', label: '低价转让' }
    ],
    
    showTypeModal: false,
    showMethodModal: false
  },

  onLoad() {
    this.loadItems();
  },

  onShow() {
    // 检查是否有新发布
    const newPublish = wx.getStorageSync('newRelayItem');
    if (newPublish) {
      wx.removeStorageSync('newRelayItem');
      this.onRefresh();
    }
  },

  // 加载好物列表
  loadItems() {
    if (this.data.isLoading || this.data.noMore) return;
    
    this.setData({ isLoading: true });

    setTimeout(() => {
      const mockItems = this.getMockItems();
      const newItems = this.data.page === 1 ? mockItems : [...this.data.items, ...mockItems];
      
      this.setData({
        items: newItems,
        isLoading: false,
        isRefreshing: false,
        noMore: this.data.page >= 2
      });
    }, 800);
  },

  // 模拟数据
  getMockItems() {
    return [
      {
        id: '1',
        title: '语言图片卡（全套）',
        condition: '9成新',
        type: '语言训练',
        ageGroup: '3-6岁',
        method: 'give',
        methodText: '赠送',
        photos: [],
        giver: { nickname: '星星妈妈' }
      },
      {
        id: '2',
        title: '感统平衡板',
        condition: '8成新',
        type: '感统训练',
        ageGroup: '3-6岁',
        method: 'give',
        methodText: '赠送',
        photos: [],
        giver: { nickname: '阳阳爸爸' }
      },
      {
        id: '3',
        title: '注意力训练教具套装',
        condition: '95成新',
        type: '注意力',
        ageGroup: '6-12岁',
        method: 'borrow',
        methodText: '借用',
        photos: [],
        giver: { nickname: '小雨妈妈' }
      },
      {
        id: '4',
        title: '社交故事绘本（10本）',
        condition: '9成新',
        type: '社交',
        ageGroup: '3-6岁',
        method: 'sell',
        methodText: '低价转',
        photos: [],
        giver: { nickname: '豆豆妈' }
      },
      {
        id: '5',
        title: '情绪脸谱卡片',
        condition: '全新',
        type: '情绪管理',
        ageGroup: '0-6岁',
        method: 'give',
        methodText: '赠送',
        photos: [],
        giver: { nickname: '匿名家长' }
      }
    ];
  },

  // 下拉刷新
  onRefresh() {
    this.setData({
      isRefreshing: true,
      page: 1,
      noMore: false,
      items: []
    });
    this.loadItems();
  },

  // 加载更多
  loadMore() {
    if (this.data.noMore || this.data.isLoading) return;
    this.setData({ page: this.data.page + 1 });
    this.loadItems();
  },

  // 年龄筛选
  selectAgeFilter(e) {
    const value = e.currentTarget.dataset.value;
    this.setData({
      'currentFilter.age': value,
      page: 1,
      items: [],
      noMore: false
    });
    this.loadItems();
  },

  // 显示类型筛选
  showTypeFilter() {
    this.setData({ showTypeModal: true });
  },

  hideTypeFilter() {
    this.setData({ showTypeModal: false });
  },

  selectTypeFilter(e) {
    const { value, label } = e.currentTarget.dataset;
    this.setData({
      'currentFilter.type': value,
      'currentFilter.typeLabel': value ? label : '',
      showTypeModal: false,
      page: 1,
      items: [],
      noMore: false
    });
    this.loadItems();
  },

  // 显示方式筛选
  showMethodFilter() {
    this.setData({ showMethodModal: true });
  },

  hideMethodFilter() {
    this.setData({ showMethodModal: false });
  },

  selectMethodFilter(e) {
    const { value, label } = e.currentTarget.dataset;
    this.setData({
      'currentFilter.method': value,
      'currentFilter.methodLabel': value ? label : '',
      showMethodModal: false,
      page: 1,
      items: [],
      noMore: false
    });
    this.loadItems();
  },

  // 跳转到详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/relay-detail/relay-detail?id=${id}`
    });
  },

  // 跳转到发布
  goToPublish() {
    wx.navigateTo({
      url: '/pages/relay-publish/relay-publish'
    });
  },

  // 返回
  goBack() {
    wx.navigateBack();
  }
});

