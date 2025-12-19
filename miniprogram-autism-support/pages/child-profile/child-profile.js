// pages/child-profile/child-profile.js
const app = getApp();

Page({
  data: {
    childProfile: {
      nickname: '',
      ageRange: '',
      gender: '',
      conditions: [],
      goals: []
    },
    ageOptions: ['0-3岁', '3-6岁', '6-12岁'],
    genderOptions: [
      { value: 'male', label: '男' },
      { value: 'female', label: '女' },
      { value: 'unknown', label: '不确定' }
    ],
    conditionOptions: ['语言发展', '社交互动', '情绪管理', '感觉统合', '行为问题', '不确定'],
    goalOptions: ['语言模仿', '眼神接触', '情绪稳定', '入园准备', '社交技能', '感统训练', '行为管理']
  },

  onLoad() {
    this.loadChildProfile();
  },

  // 加载已有档案
  loadChildProfile() {
    const savedProfile = wx.getStorageSync('childProfile');
    if (savedProfile) {
      this.setData({
        childProfile: {
          ...this.data.childProfile,
          ...savedProfile
        }
      });
    }
  },

  // 昵称输入
  onNicknameInput(e) {
    this.setData({
      'childProfile.nickname': e.detail.value
    });
  },

  // 选择年龄
  selectAge(e) {
    this.setData({
      'childProfile.ageRange': e.currentTarget.dataset.value
    });
  },

  // 选择性别
  selectGender(e) {
    this.setData({
      'childProfile.gender': e.currentTarget.dataset.value
    });
  },

  // 切换状况
  toggleCondition(e) {
    const value = e.currentTarget.dataset.value;
    let conditions = [...this.data.childProfile.conditions];
    
    const index = conditions.indexOf(value);
    if (index > -1) {
      conditions.splice(index, 1);
    } else {
      conditions.push(value);
    }
    
    this.setData({
      'childProfile.conditions': conditions
    });
  },

  // 切换目标
  toggleGoal(e) {
    const value = e.currentTarget.dataset.value;
    let goals = [...this.data.childProfile.goals];
    
    const index = goals.indexOf(value);
    if (index > -1) {
      goals.splice(index, 1);
    } else {
      goals.push(value);
    }
    
    this.setData({
      'childProfile.goals': goals
    });
  },

  // 保存档案
  saveProfile() {
    const { childProfile } = this.data;
    
    // 验证必填项
    if (!childProfile.ageRange) {
      wx.showToast({ title: '请选择年龄段', icon: 'none' });
      return;
    }

    wx.setStorageSync('childProfile', childProfile);
    app.globalData.childProfile = childProfile;

    wx.showToast({
      title: '保存成功',
      icon: 'success',
      success: () => {
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    });
  },

  // 返回
  goBack() {
    wx.navigateBack();
  }
});

