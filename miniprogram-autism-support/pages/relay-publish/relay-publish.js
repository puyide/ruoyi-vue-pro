// pages/relay-publish/relay-publish.js
const app = getApp();

Page({
  data: {
    photos: [],
    title: '',
    condition: '',
    type: '',
    ageGroup: '',
    description: '',
    method: 'give',
    delivery: 'face_to_face',
    canPublish: false,

    conditionOptions: [
      { value: 'brand_new', label: '全新' },
      { value: '95_new', label: '95成新' },
      { value: '9_new', label: '9成新' },
      { value: '8_new', label: '8成新' },
      { value: '7_new', label: '7成新及以下' }
    ],

    typeOptions: [
      { value: 'language', label: '语言训练' },
      { value: 'sensory', label: '感统训练' },
      { value: 'attention', label: '注意力' },
      { value: 'social', label: '社交' },
      { value: 'emotion', label: '情绪管理' },
      { value: 'other', label: '其他' }
    ],

    ageOptions: [
      { value: '0-3', label: '0-3岁' },
      { value: '3-6', label: '3-6岁' },
      { value: '6-12', label: '6-12岁' },
      { value: 'all', label: '不限' }
    ]
  },

  // 选择照片
  choosePhoto() {
    const remainCount = 6 - this.data.photos.length;
    wx.chooseMedia({
      count: remainCount,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newPhotos = res.tempFiles.map(file => file.tempFilePath);
        this.setData({
          photos: [...this.data.photos, ...newPhotos]
        });
        this.checkCanPublish();
      }
    });
  },

  // 删除照片
  removePhoto(e) {
    const index = e.currentTarget.dataset.index;
    const photos = [...this.data.photos];
    photos.splice(index, 1);
    this.setData({ photos });
    this.checkCanPublish();
  },

  // 标题输入
  onTitleInput(e) {
    this.setData({ title: e.detail.value });
    this.checkCanPublish();
  },

  // 选择新旧程度
  selectCondition(e) {
    this.setData({ condition: e.currentTarget.dataset.value });
    this.checkCanPublish();
  },

  // 选择类型
  selectType(e) {
    this.setData({ type: e.currentTarget.dataset.value });
    this.checkCanPublish();
  },

  // 选择年龄
  selectAge(e) {
    this.setData({ ageGroup: e.currentTarget.dataset.value });
    this.checkCanPublish();
  },

  // 说明输入
  onDescInput(e) {
    this.setData({ description: e.detail.value });
  },

  // 选择赠送方式
  selectMethod(e) {
    this.setData({ method: e.currentTarget.dataset.value });
  },

  // 选择交付方式
  selectDelivery(e) {
    this.setData({ delivery: e.currentTarget.dataset.value });
  },

  // 检查是否可发布
  checkCanPublish() {
    const { photos, title, condition, type, ageGroup } = this.data;
    const canPublish = photos.length > 0 && 
                       title.trim().length >= 2 && 
                       condition && 
                       type && 
                       ageGroup;
    this.setData({ canPublish });
  },

  // 发布
  publish() {
    if (!this.data.canPublish) {
      let msg = '';
      if (this.data.photos.length === 0) msg = '请上传至少一张照片';
      else if (this.data.title.trim().length < 2) msg = '请输入好物名称';
      else if (!this.data.condition) msg = '请选择新旧程度';
      else if (!this.data.type) msg = '请选择好物类型';
      else if (!this.data.ageGroup) msg = '请选择适用年龄';
      
      wx.showToast({ title: msg, icon: 'none' });
      return;
    }

    wx.showLoading({ title: '发布中...' });

    // 模拟发布
    setTimeout(() => {
      wx.hideLoading();
      
      wx.setStorageSync('newRelayItem', true);

      wx.showModal({
        title: '发布成功',
        content: '您的好物信息已提交审核，审核通过后将展示在列表中',
        showCancel: false,
        success: () => {
          wx.navigateBack();
        }
      });
    }, 1500);
  },

  // 返回
  goBack() {
    const hasContent = this.data.photos.length > 0 || 
                       this.data.title || 
                       this.data.description;
    
    if (hasContent) {
      wx.showModal({
        title: '确认返回？',
        content: '返回后编辑内容将不会保存',
        success: (res) => {
          if (res.confirm) {
            wx.navigateBack();
          }
        }
      });
    } else {
      wx.navigateBack();
    }
  }
});

