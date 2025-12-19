// pages/institution-detail/institution-detail.js
const app = getApp();

Page({
  data: {
    institutionId: '',
    institution: null,
    reviews: []
  },

  onLoad(options) {
    this.setData({ institutionId: options.id });
    this.loadInstitutionDetail();
    this.loadReviews();
  },

  // 加载机构详情
  loadInstitutionDetail() {
    const mockInstitution = {
      id: this.data.institutionId,
      name: '星星康复中心',
      images: [],
      tags: ['感统', '语言', '行为'],
      address: '成都市武侯区科华北路88号',
      phone: '028-88888888',
      openTime: '周一至周五 9:00-18:00',
      rating: 4.6,
      ratings: {
        teacher: 4.8,
        environment: 4.5,
        transparency: 4.2,
        effect: 4.6
      }
    };

    this.setData({ institution: mockInstitution });
  },

  // 加载评价
  loadReviews() {
    const mockReviews = [
      {
        id: '1',
        author: { nickname: '匿名家长' },
        rating: 5,
        content: '老师很耐心，对孩子非常有爱心。训练方法也很专业，孩子进步很明显。',
        timeAgo: '1周前'
      },
      {
        id: '2',
        author: { nickname: '匿名家长' },
        rating: 4,
        content: '环境还不错，设施齐全。就是价格有点贵，希望能有一些优惠政策。',
        timeAgo: '2周前'
      },
      {
        id: '3',
        author: { nickname: '匿名家长' },
        rating: 5,
        content: '推荐！我们在这里训练了半年，孩子的语言能力有了很大提升。',
        timeAgo: '1个月前'
      }
    ];

    this.setData({ reviews: mockReviews });
  },

  // 返回
  goBack() {
    wx.navigateBack();
  },

  // 分享机构
  shareInstitution() {
    // 触发分享
  },

  // 打开地图导航
  openMap() {
    wx.openLocation({
      latitude: 30.5702,
      longitude: 104.0665,
      name: this.data.institution.name,
      address: this.data.institution.address
    });
  },

  // 拨打电话
  callPhone() {
    wx.makePhoneCall({
      phoneNumber: this.data.institution.phone
    });
  },

  // 查看全部评价
  viewAllReviews() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    });
  },

  // 信息纠错
  reportError() {
    wx.showModal({
      title: '信息纠错',
      content: '发现信息有误？请联系我们进行更正。',
      confirmText: '联系客服',
      success: (res) => {
        if (res.confirm) {
          // 打开客服会话
          wx.showToast({
            title: '请联系客服',
            icon: 'none'
          });
        }
      }
    });
  },

  // 写评价
  writeReview() {
    wx.showModal({
      title: '写评价',
      content: '评价功能即将开放，敬请期待！',
      showCancel: false
    });
  },

  // 分享
  onShareAppMessage() {
    return {
      title: `${this.data.institution.name} - 星语家园`,
      path: `/pages/institution-detail/institution-detail?id=${this.data.institutionId}`
    };
  }
});

