// pages/timer/timer.js
Page({
  data: {
    durationOptions: [
      { value: 60, label: '1分钟' },
      { value: 180, label: '3分钟' },
      { value: 300, label: '5分钟' },
      { value: 0, label: '自定义' }
    ],
    selectedDuration: 180,
    customMinutes: '',
    showCustomInput: false,
    totalSeconds: 180,
    remainingSeconds: 180,
    isRunning: false,
    timer: null,
    displayTime: '03:00',
    progress: 360,
    progressPercent: 100,
    timerColor: '#FF7A45',
    showCompleteModal: false
  },

  onLoad() {
    this.updateDisplay();
  },

  onUnload() {
    this.clearTimer();
  },

  // 选择时长
  selectDuration(e) {
    const value = e.currentTarget.dataset.value;
    
    if (value === 0) {
      this.setData({
        selectedDuration: 0,
        showCustomInput: true
      });
      return;
    }

    this.setData({
      selectedDuration: value,
      showCustomInput: false,
      totalSeconds: value,
      remainingSeconds: value,
      isRunning: false
    });
    
    this.clearTimer();
    this.updateDisplay();
  },

  // 自定义时长输入
  onCustomInput(e) {
    const minutes = parseInt(e.detail.value) || 0;
    const seconds = Math.min(minutes, 60) * 60;
    
    this.setData({
      customMinutes: e.detail.value,
      totalSeconds: seconds,
      remainingSeconds: seconds
    });
    
    this.updateDisplay();
  },

  // 开始/暂停计时器
  toggleTimer() {
    if (this.data.isRunning) {
      this.pauseTimer();
    } else {
      this.startTimer();
    }
  },

  // 开始计时
  startTimer() {
    if (this.data.remainingSeconds <= 0) {
      this.resetTimer();
    }

    this.setData({ isRunning: true });
    
    this.timer = setInterval(() => {
      const remaining = this.data.remainingSeconds - 1;
      
      if (remaining <= 0) {
        this.completeTimer();
        return;
      }

      this.setData({ remainingSeconds: remaining });
      this.updateDisplay();
    }, 1000);
  },

  // 暂停计时
  pauseTimer() {
    this.setData({ isRunning: false });
    this.clearTimer();
  },

  // 重置计时器
  resetTimer() {
    this.clearTimer();
    this.setData({
      remainingSeconds: this.data.totalSeconds,
      isRunning: false
    });
    this.updateDisplay();
  },

  // 完成计时
  completeTimer() {
    this.clearTimer();
    this.setData({
      remainingSeconds: 0,
      isRunning: false,
      showCompleteModal: true
    });
    this.updateDisplay();
    
    // 震动提醒
    wx.vibrateShort({ type: 'heavy' });
  },

  // 清除定时器
  clearTimer() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },

  // 更新显示
  updateDisplay() {
    const { remainingSeconds, totalSeconds } = this.data;
    
    const minutes = Math.floor(remainingSeconds / 60);
    const seconds = remainingSeconds % 60;
    const displayTime = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    
    const progressPercent = totalSeconds > 0 ? (remainingSeconds / totalSeconds) * 100 : 0;
    const progress = (progressPercent / 100) * 360;
    
    // 根据进度改变颜色
    let timerColor = '#FF7A45';
    if (progressPercent < 20) {
      timerColor = '#FF6B6B';
    } else if (progressPercent < 50) {
      timerColor = '#FDCB6E';
    }

    this.setData({
      displayTime,
      progress,
      progressPercent,
      timerColor
    });
  },

  // 格式化时间
  formatTime(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  },

  // 关闭完成弹窗
  closeCompleteModal() {
    this.setData({ showCompleteModal: false });
    this.resetTimer();
  },

  // 返回
  goBack() {
    this.clearTimer();
    wx.navigateBack();
  }
});

