# 🏥 康复训练器材共享平台

> **将商城模块改造为自闭症康复训练器材的赠送/借用平台**

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Status](https://img.shields.io/badge/status-development-yellow.svg)]()
[![Progress](https://img.shields.io/badge/progress-65%25-green.svg)]()

---

## 🎯 项目目标

将芋道商城(`yudao-module-mall`)改造为一个有爱心的**康复训练器材共享平台**,帮助自闭症儿童家庭:

- 💝 **赠送闲置器材** - 将用过的康复训练器材赠送给需要的家庭
- 🔄 **借用康复器材** - 短期借用昂贵的康复训练器材
- 🏆 **信用体系** - 通过信用分机制促进良性互动
- 📍 **就近匹配** - 支持同城/同区域器材共享

---

## ✨ 核心功能

### 1. 器材管理
- 📦 发布器材信息(名称、照片、状态、适用年龄)
- 🏷️ 器材分类(感统/语言/认知/社交/精细动作/大运动)
- 📊 器材状态(全新/九成新/八成新/七成新以下)
- 🔍 智能搜索和筛选

### 2. 赠送流程
```
发布赠送 → 他人申请 → 确认赠送 → 物流/自提 → 确认收货 → 完成
                                                    ↓
                                                  +10信用分
```

### 3. 借用流程
```
发布借用 → 预约借用 → 支付押金 → 取货 → 使用 → 归还 → 退还押金
                                                  ↓
                                          按时归还: +5分
                                          逾期归还: -10分
                                          器材损坏: -20分
```

### 4. 信用体系
- 🌟 默认信用分: 100分
- ➕ 增加信用分: 捐赠(+10)、按时归还(+5)、优质评价(+3)
- ➖ 减少信用分: 逾期(-10)、损坏(-20)、违规(-30)
- 📈 信用分影响: 低于60分限制借用

### 5. 预约系统
- 📅 提前预约器材
- ✅ 拥有者审核
- 🔔 到期提醒
- ⏰ 自动超时取消

---

## 📁 项目结构

```
ruoyi-vue-pro/
├── sql/                                          # 数据库脚本
│   ├── postgresql/
│   │   └── equipment_renovation.sql             # PostgreSQL改造脚本 ⭐
│   └── mysql/
│       └── equipment_renovation.sql             # MySQL改造脚本 ⭐
│
├── yudao-module-mall/                           # 商城模块(改造为器材共享)
│   ├── yudao-module-product/                   # 商品模块 → 器材模块
│   │   └── src/main/java/.../product/
│   │       ├── enums/equipment/                # 器材相关枚举 ⭐
│   │       │   ├── EquipmentStatusEnum.java   # 器材状态
│   │       │   ├── ShareTypeEnum.java         # 共享类型
│   │       │   └── TrainingTypeEnum.java      # 训练类型
│   │       ├── dal/dataobject/reservation/    # 预约相关DO ⭐
│   │       │   └── EquipmentReservationDO.java
│   │       └── dal/mysql/reservation/         # 预约Mapper ⭐
│   │           └── EquipmentReservationMapper.java
│   │
│   └── yudao-module-trade/                     # 交易模块 → 赠送/借用模块
│       └── src/main/java/.../trade/
│           └── enums/order/
│               └── ReturnStatusEnum.java       # 归还状态枚举 ⭐
│
├── yudao-module-member/                         # 会员模块
│   └── src/main/java/.../member/
│       ├── enums/credit/                       # 信用分枚举 ⭐
│       │   └── CreditChangeTypeEnum.java
│       ├── dal/dataobject/credit/             # 信用分DO ⭐
│       │   ├── MemberCreditScoreDO.java
│       │   └── MemberCreditLogDO.java
│       ├── dal/mysql/credit/                  # 信用分Mapper ⭐
│       │   ├── MemberCreditScoreMapper.java
│       │   └── MemberCreditLogMapper.java
│       └── controller/admin/credit/vo/        # 信用分VO ⭐
│           └── CreditLogPageReqVO.java
│
├── yudao-ui-admin-vue3/                        # 管理后台前端
│   └── src/
│       ├── views/mall/                        # 商城页面 → 器材共享页面
│       │   ├── product/spu/                   # 器材管理(需修改) 🔄
│       │   ├── trade/order/                   # 订单管理(需修改) 🔄
│       │   └── reservation/                   # 预约管理(新建) ⏳
│       ├── views/member/credit/               # 信用分管理(新建) ⏳
│       └── utils/dict.ts                      # 字典配置(需添加) 🔄
│
└── 📚 文档/
    ├── EQUIPMENT_RENOVATION_GUIDE.md          # 完整改造指南 ⭐
    ├── RENOVATION_PROGRESS.md                 # 进度报告 ⭐
    ├── QUICK_START.md                         # 快速启动 ⭐
    └── EQUIPMENT_SHARING_PLATFORM.md          # 本文档 ⭐

⭐ = 已完成   🔄 = 需修改   ⏳ = 待创建
```

---

## 📊 数据库设计

### 新增表

#### 1. member_credit_score (会员信用分表)
```sql
- id                      主键
- user_id                 用户ID
- total_score             总信用分(默认100)
- donate_count            捐赠次数
- borrow_count            借用次数
- return_on_time_count    按时归还次数
- overdue_count           逾期次数
- damage_count            损坏次数
```

#### 2. member_credit_log (信用分变动记录表)
```sql
- id                      主键
- user_id                 用户ID
- change_type             变动类型
- change_score            变动分数
- before_score            变动前分数
- after_score             变动后分数
- reason                  变动原因
- related_order_id        关联订单ID
```

#### 3. equipment_reservation (器材预约表)
```sql
- id                      主键
- equipment_id            器材ID
- user_id                 预约用户ID
- reservation_status      预约状态
- plan_borrow_date        计划借用日期
- plan_return_date        计划归还日期
- owner_user_id           器材拥有者ID
```

### 扩展表

#### product_spu (新增10个字段)
```sql
+ equipment_status        器材状态
+ suitable_age_range      适用年龄段
+ training_types          训练类型
+ usage_count             使用次数
+ last_disinfection_date  最近消毒时间
+ owner_user_id           器材拥有者
+ share_type              共享类型
+ deposit_amount          押金金额
+ borrow_duration         建议借用天数
+ equipment_location      器材位置
```

#### trade_order (新增10个字段)
```sql
+ share_type              订单类型(赠送/借用)
+ borrow_start_date       借用开始时间
+ borrow_end_date         计划归还时间
+ actual_return_date      实际归还时间
+ return_status           归还状态
+ deposit_refund_status   押金退还状态
+ equipment_condition     器材状态说明
+ equipment_photos        器材照片
+ overdue_days            逾期天数
+ damage_compensation     损坏赔偿金额
```

---

## 🚀 快速开始

### 前置要求
- Java 17+
- PostgreSQL 12+ / MySQL 5.7+
- Node.js 16+
- Vue 3

### 步骤1: 执行数据库改造

```bash
# PostgreSQL
psql -U your_user -d your_db -f sql/postgresql/equipment_renovation.sql

# MySQL
mysql -u your_user -p your_db < sql/mysql/equipment_renovation.sql
```

### 步骤2: 启动后端服务

```bash
cd yudao-server
mvn clean package
java -jar target/yudao-server.jar
```

### 步骤3: 启动前端

```bash
cd yudao-ui-admin-vue3
pnpm install
pnpm dev
```

### 步骤4: 访问系统

- 管理后台: http://localhost
- 默认账号: admin / admin123

---

## 📖 详细文档

| 文档 | 说明 | 适合人群 |
|------|------|----------|
| [📘 EQUIPMENT_RENOVATION_GUIDE.md](EQUIPMENT_RENOVATION_GUIDE.md) | 完整技术实施指南 | 开发人员 |
| [📊 RENOVATION_PROGRESS.md](RENOVATION_PROGRESS.md) | 当前进度和计划 | 项目经理 |
| [🚀 QUICK_START.md](QUICK_START.md) | 5分钟快速上手 | 所有人 |

---

## ✅ 当前进度

### 已完成 (65%)

- ✅ 数据库设计和脚本
- ✅ 后端枚举类(6个)
- ✅ 后端DO对象(3个)
- ✅ 后端Mapper接口(3个)
- ✅ 基础VO类(2个)
- ✅ 完整技术文档

### 进行中

- 🔄 后端Service层实现
- 🔄 后端Controller层实现

### 待开始

- ⏳ 前端页面改造
- ⏳ 定时任务开发
- ⏳ 单元测试
- ⏳ 集成测试

---

## 🎨 界面预览 (规划)

### 器材列表页
```
+----------------------------------------------------------+
| 🔍 搜索: [器材名称]  器材状态: [全部▾]  共享类型: [全部▾] |
+----------------------------------------------------------+
| 器材图片 | 器材名称       | 状态   | 共享类型 | 适用年龄 | 操作    |
|----------|--------------|--------|----------|----------|---------|
| [图]     | 感统训练球   | 九成新 | 仅借用   | 3-6岁    | [详情]  |
| [图]     | 语言卡片     | 全新   | 赠送     | 2-5岁    | [申请]  |
| [图]     | 认知拼图     | 八成新 | 两者都可 | 4-7岁    | [预约]  |
+----------------------------------------------------------+
```

### 信用分管理页
```
+----------------------------------------------------------+
| 用户信息     | 总信用分 | 捐赠次数 | 借用次数 | 逾期次数 |
|-------------|---------|---------|---------|----------|
| 张三(会员)   | 🌟 105分 | 2次     | 5次     | 0次      |
| 李四(会员)   | 🌟 95分  | 1次     | 3次     | 1次      |
| 王五(会员)   | ⚠️ 55分  | 0次     | 2次     | 3次      |
+----------------------------------------------------------+
```

---

## 🔮 未来规划

### v1.1 (Q1 2026)
- [ ] 手机端H5页面
- [ ] 微信小程序
- [ ] 短信/邮件通知
- [ ] 地图定位

### v1.2 (Q2 2026)
- [ ] 在线支付(微信/支付宝)
- [ ] 物流追踪
- [ ] 用户评价系统
- [ ] 智能推荐

### v2.0 (Q3 2026)
- [ ] AI智能匹配
- [ ] 社区论坛
- [ ] 康复知识库
- [ ] 专家在线咨询

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request!

### 开发流程
1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情

---

## 💖 致谢

- [芋道源码](https://github.com/YunaiV/ruoyi-vue-pro) - 提供了优秀的基础框架
- 所有为自闭症儿童康复事业贡献的爱心人士

---

## 📞 联系我们

- 💬 交流群: [待创建]
- 📧 邮箱: [您的邮箱]
- 🌐 官网: [待建设]

---

<p align="center">
  <b>让每个自闭症儿童都能获得需要的康复训练器材！</b><br>
  <i>Love, Share, Care ❤️</i>
</p>

---

**最后更新**: 2025-12-15  
**当前版本**: v1.0.0-alpha  
**开发状态**: 🚧 开发中

