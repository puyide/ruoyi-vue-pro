// pages/post-create/post-create.js
const app = getApp();
const api = require('../../utils/api');

Page({
  data: {
    title: '',
    content: '',
    images: [],
    selectedTags: [],
    selectedCid: null,
    isAnonymous: true,
    
    // 标签选项（会从分类动态加载）
    tagOptions: ['语言训练', '情绪管理', '入园准备', '感统训练', '社交技能', '居家干预', '0-3岁', '3-6岁', '6-12岁'],
    
    // 分类选项
    categories: [],
    categoryIndex: 0,
    
    canPublish: false,
    isPublishing: false
  },

  onLoad() {
    this.loadCategories();
  },

  // 加载分类列表
  async loadCategories() {
    try {
      const categories = await api.getCommunityCategories();
      if (categories && categories.length > 0) {
        this.setData({
          categories,
          selectedCid: categories[0].cid
        });
      }
    } catch (err) {
      console.error('加载分类失败:', err);
    }
  },

  // 标题输入
  onTitleInput(e) {
    this.setData({ title: e.detail.value });
    this.checkCanPublish();
  },

  // 正文输入
  onContentInput(e) {
    this.setData({ content: e.detail.value });
    this.checkCanPublish();
  },

  // 选择分类
  onCategoryChange(e) {
    const index = e.detail.value;
    this.setData({
      categoryIndex: index,
      selectedCid: this.data.categories[index]?.cid
    });
  },

  // 选择图片
  chooseImage() {
    const remainCount = 6 - this.data.images.length;
    if (remainCount <= 0) {
      wx.showToast({ title: '最多上传6张图片', icon: 'none' });
      return;
    }
    
    wx.chooseMedia({
      count: remainCount,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newImages = res.tempFiles.map(file => ({
          tempFilePath: file.tempFilePath,
          url: null, // 上传后填充
          uploading: false
        }));
        
        this.setData({
          images: [...this.data.images, ...newImages]
        });
        
        // 上传图片
        newImages.forEach((_, idx) => {
          this.uploadImage(this.data.images.length - newImages.length + idx);
        });
      }
    });
  },

  // 上传单张图片
  async uploadImage(index) {
    const images = [...this.data.images];
    images[index].uploading = true;
    this.setData({ images });
    
    try {
      // 调用文件上传接口
      const token = wx.getStorageSync('token');
      const result = await new Promise((resolve, reject) => {
        wx.uploadFile({
          url: `${api.config.baseUrl}/infra/file/upload`,
          filePath: images[index].tempFilePath,
          name: 'file',
          header: {
            'Authorization': token ? `Bearer ${token}` : ''
          },
          success: (res) => {
            try {
              const data = JSON.parse(res.data);
              if (data.code === 0) {
                resolve(data.data);
              } else {
                reject(new Error(data.msg || '上传失败'));
              }
            } catch (e) {
              reject(e);
            }
          },
          fail: reject
        });
      });
      
      images[index].url = result;
      images[index].uploading = false;
      this.setData({ images });
    } catch (err) {
      console.error('图片上传失败:', err);
      images[index].uploading = false;
      this.setData({ images });
      wx.showToast({ title: '图片上传失败', icon: 'none' });
    }
  },

  // 删除图片
  removeImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = [...this.data.images];
    images.splice(index, 1);
    this.setData({ images });
  },

  // 切换标签
  toggleTag(e) {
    const tag = e.currentTarget.dataset.tag;
    let selectedTags = [...this.data.selectedTags];
    
    const index = selectedTags.indexOf(tag);
    if (index > -1) {
      selectedTags.splice(index, 1);
    } else {
      if (selectedTags.length >= 5) {
        wx.showToast({ title: '最多选择5个标签', icon: 'none' });
        return;
      }
      selectedTags.push(tag);
    }
    
    this.setData({ selectedTags });
    this.checkCanPublish();
  },

  // 切换匿名
  toggleAnonymous(e) {
    this.setData({ isAnonymous: e.detail.value });
  },

  // 检查是否可发布
  checkCanPublish() {
    const { title, content, selectedTags, selectedCid, images } = this.data;
    
    // 检查是否有图片正在上传
    const hasUploadingImages = images.some(img => img.uploading);
    
    const canPublish = 
      title.trim().length >= 5 && 
      content.trim().length >= 20 && 
      selectedTags.length > 0 && 
      selectedCid && 
      !hasUploadingImages;
    
    this.setData({ canPublish });
  },

  // 发布
  async publish() {
    if (!this.data.canPublish || this.data.isPublishing) {
      let msg = '';
      if (this.data.title.trim().length < 5) {
        msg = '标题至少需要5个字';
      } else if (this.data.content.trim().length < 20) {
        msg = '正文至少需要20个字';
      } else if (this.data.selectedTags.length === 0) {
        msg = '请至少选择一个标签';
      } else if (!this.data.selectedCid) {
        msg = '请选择分类';
      } else if (this.data.images.some(img => img.uploading)) {
        msg = '请等待图片上传完成';
      }
      
      if (msg) {
        wx.showToast({ title: msg, icon: 'none' });
      }
      return;
    }

    this.setData({ isPublishing: true });
    wx.showLoading({ title: '发布中...' });

    try {
      // 收集已上传的图片URL
      const imageUrls = this.data.images
        .filter(img => img.url)
        .map(img => img.url);
      
      const tid = await api.createCommunityTopic({
        title: this.data.title.trim(),
        content: this.data.content.trim(),
        cid: this.data.selectedCid,
        tags: this.data.selectedTags,
        images: imageUrls,
        anonymous: this.data.isAnonymous
      });
      
      wx.hideLoading();
      
      // 标记有新帖子
      wx.setStorageSync('newPost', true);
      
      wx.showModal({
        title: '发布成功',
        content: '帖子已发布',
        showCancel: false,
        success: () => {
          wx.navigateBack();
        }
      });
    } catch (err) {
      wx.hideLoading();
      console.error('发布失败:', err);
      
      wx.showModal({
        title: '发布失败',
        content: err.msg || '请稍后重试',
        showCancel: false
      });
    } finally {
      this.setData({ isPublishing: false });
    }
  },

  // 返回
  goBack() {
    if (this.data.title || this.data.content || this.data.images.length > 0) {
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
