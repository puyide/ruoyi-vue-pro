// pages/resource/resource.js
const app = getApp();

Page({
  data: {
    currentCity: '成都',
    cities: ['成都', '北京', '上海', '广州', '深圳', '杭州', '南京', '武汉', '重庆', '西安'],
    showCitySelector: false,
    filterTags: [
      { id: 'all', name: '全部' },
      { id: 'sensory', name: '感统' },
      { id: 'language', name: '语言' },
      { id: 'behavior', name: '行为' },
      { id: 'social', name: '社交' },
      { id: 'comprehensive', name: '综合' }
    ],
    currentTag: 'all',
    institutions: [],
    page: 1,
    pageSize: 10,
    isLoading: false,
    isRefreshing: false,
    noMore: false
  },

  onLoad() {
    this.loadInstitutions();
  },

  // 加载机构列表
  loadInstitutions() {
    if (this.data.isLoading || this.data.noMore) return;

    this.setData({ isLoading: true });

    setTimeout(() => {
      const mockInstitutions = [
        {
          id: '1',
          name: '星星康复中心',
          coverImage: '',
          tags: ['感统', '语言', '行为'],
          rating: 4.6,
          reviewCount: 32,
          district: '武侯区',
          distance: '2.3km',
          highlights: ['师资优秀', '环境好']
        },
        {
          id: '2',
          name: '阳光特殊教育学校',
          coverImage: '',
          tags: ['语言', '社交', '综合'],
          rating: 4.8,
          reviewCount: 56,
          district: '锦江区',
          distance: '3.1km',
          highlights: ['小班教学', '跟踪服务']
        },
        {
          id: '3',
          name: '彩虹桥康复训练中心',
          coverImage: '',
          tags: ['感统', '行为'],
          rating: 4.5,
          reviewCount: 28,
          district: '青羊区',
          distance: '4.5km',
          highlights: ['价格实惠', '地铁直达']
        },
        {
          id: '4',
          name: '明日之星儿童发展中心',
          coverImage: '',
          tags: ['语言', '感统', '社交'],
          rating: 4.7,
          reviewCount: 45,
          district: '高新区',
          distance: '5.8km',
          highlights: ['专业评估', '个性化方案']
        },
        {
          id: '5',
          name: '希望之光康复机构',
          coverImage: '',
          tags: ['行为', '社交'],
          rating: 4.4,
          reviewCount: 19,
          district: '成华区',
          distance: '6.2km',
          highlights: ['家长培训', '效果可见']
        }
      ];

      const newInstitutions = this.data.page === 1 
        ? mockInstitutions 
        : [...this.data.institutions, ...mockInstitutions];

      this.setData({
        institutions: newInstitutions,
        isLoading: false,
        isRefreshing: false,
        noMore: this.data.page >= 2
      });
    }, 800);
  },

  // 下拉刷新
  onRefresh() {
    this.setData({
      isRefreshing: true,
      page: 1,
      noMore: false,
      institutions: []
    });
    this.loadInstitutions();
  },

  // 加载更多
  loadMore() {
    if (this.data.noMore || this.data.isLoading) return;
    
    this.setData({ page: this.data.page + 1 });
    this.loadInstitutions();
  },

  // 显示城市选择器
  showCityPicker() {
    this.setData({ showCitySelector: true });
  },

  // 隐藏城市选择器
  hideCityPicker() {
    this.setData({ showCitySelector: false });
  },

  // 选择城市
  selectCity(e) {
    const city = e.currentTarget.dataset.city;
    this.setData({
      currentCity: city,
      showCitySelector: false,
      page: 1,
      institutions: [],
      noMore: false
    });
    this.loadInstitutions();
  },

  // 显示筛选
  showFilter() {
    wx.showToast({
      title: '筛选功能开发中',
      icon: 'none'
    });
  },

  // 选择标签
  selectTag(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({
      currentTag: id,
      page: 1,
      institutions: [],
      noMore: false
    });
    this.loadInstitutions();
  },

  // 跳转到详情页
  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/institution-detail/institution-detail?id=${id}`
    });
  }
});

