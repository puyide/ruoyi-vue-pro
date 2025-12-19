# 康复训练器材共享平台 - 改造指南

> 将芋道商城模块改造为自闭症康复训练器材的赠送/借用平台

## 📋 改造概述

本改造将 `yudao-module-mall` 商城模块转变为一个**康复训练器材共享平台**，用于支持自闭症康复训练器材的赠送和借用管理。

### 核心功能

- ✅ **器材管理**: 发布、管理康复训练器材
- ✅ **赠送功能**: 将闲置器材赠送给需要的家庭
- ✅ **借用功能**: 支持器材借用、归还、押金管理
- ✅ **信用体系**: 用户信用分管理，促进良性互动
- ✅ **预约系统**: 器材借用预约与审核

---

## 🗂️ 改造方案: 轻量改造(推荐)

### 为什么选择轻量改造？

1. **快速上线**: 无需大规模重构，2-3周即可完成
2. **风险可控**: 基于成熟的商城架构，稳定性高
3. **易于维护**: 保留原有代码结构，降低学习成本
4. **扩展性强**: 后续可按需深度定制

---

## 📊 数据库改造

### 执行顺序

1. **备份现有数据库** ⚠️ 重要！
   ```bash
   # PostgreSQL
   pg_dump -U username -d database_name > backup_$(date +%Y%m%d).sql
   
   # MySQL
   mysqldump -u username -p database_name > backup_$(date +%Y%m%d).sql
   ```

2. **执行改造脚本**
   ```bash
   # PostgreSQL
   psql -U username -d database_name -f sql/postgresql/equipment_renovation.sql
   
   # MySQL
   mysql -u username -p database_name < sql/mysql/equipment_renovation.sql
   ```

3. **验证数据结构**
   ```sql
   -- 检查新增字段
   DESC product_spu;
   DESC trade_order;
   
   -- 检查新建表
   DESC member_credit_score;
   DESC member_credit_log;
   DESC equipment_reservation;
   ```

### 新增数据表

| 表名 | 说明 | 用途 |
|------|------|------|
| `member_credit_score` | 会员信用分表 | 记录用户信用分及统计数据 |
| `member_credit_log` | 信用分变动记录 | 记录每次信用分变动的详情 |
| `equipment_reservation` | 器材预约表 | 管理借用预约申请 |

### 修改的数据表

#### 1. product_spu (商品表 → 器材表)

| 新增字段 | 类型 | 说明 |
|----------|------|------|
| `equipment_status` | TINYINT | 器材状态(1-全新, 2-九成新, 3-八成新, 4-七成新以下) |
| `suitable_age_range` | VARCHAR(50) | 适用年龄段 |
| `training_types` | VARCHAR(200) | 训练类型(多选,逗号分隔) |
| `usage_count` | INT | 使用次数 |
| `last_disinfection_date` | DATETIME | 最近消毒时间 |
| `owner_user_id` | BIGINT | 器材拥有者ID |
| `share_type` | TINYINT | 共享类型(1-赠送, 2-借用, 3-两者都可) |
| `deposit_amount` | INT | 押金金额(分) |
| `borrow_duration` | INT | 建议借用天数 |
| `equipment_location` | VARCHAR(200) | 器材位置 |

#### 2. trade_order (订单表 → 赠送/借用订单表)

| 新增字段 | 类型 | 说明 |
|----------|------|------|
| `share_type` | TINYINT | 订单类型(1-赠送, 2-借用) |
| `borrow_start_date` | DATETIME | 借用开始时间 |
| `borrow_end_date` | DATETIME | 计划归还时间 |
| `actual_return_date` | DATETIME | 实际归还时间 |
| `return_status` | TINYINT | 归还状态(0-未归还, 1-已归还, 2-逾期, 3-已损坏) |
| `deposit_refund_status` | TINYINT | 押金退还状态 |
| `equipment_condition` | TEXT | 器材状态说明 |
| `equipment_photos` | VARCHAR(1000) | 器材照片(JSON数组) |
| `overdue_days` | INT | 逾期天数 |
| `damage_compensation` | INT | 损坏赔偿(分) |

---

## 🔧 后端改造

### 第一步: 创建枚举类

#### 1. 器材状态枚举
**位置**: `yudao-module-mall/yudao-module-product/src/main/java/cn/iocoder/yudao/module/product/enums/`

```java
EquipmentStatusEnum.java
- NEW(1, "全新")
- NINETY_PERCENT_NEW(2, "九成新")
- EIGHTY_PERCENT_NEW(3, "八成新")
- SEVENTY_PERCENT_NEW(4, "七成新以下")
```

#### 2. 共享类型枚举
```java
ShareTypeEnum.java
- DONATE_ONLY(1, "仅赠送")
- BORROW_ONLY(2, "仅借用")
- BOTH(3, "赠送或借用")
```

#### 3. 归还状态枚举
```java
ReturnStatusEnum.java
- NOT_RETURNED(0, "未归还")
- RETURNED(1, "已归还")
- OVERDUE(2, "逾期未还")
- DAMAGED(3, "已损坏")
```

### 第二步: 更新 DO 对象

#### 1. ProductSpuDO.java
添加新字段的 getter/setter，使用 Lombok 注解。

#### 2. TradeOrderDO.java
添加借用相关字段。

#### 3. 创建新的 DO
- `MemberCreditScoreDO.java`
- `MemberCreditLogDO.java`
- `EquipmentReservationDO.java`

### 第三步: 创建 Mapper

```
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/dal/mysql/credit/
├── MemberCreditScoreMapper.java
└── MemberCreditLogMapper.java

yudao-module-mall/yudao-module-product/src/main/java/cn/iocoder/yudao/module/product/dal/mysql/reservation/
└── EquipmentReservationMapper.java
```

### 第四步: 创建 Service

#### 信用分服务
```
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/service/credit/
├── MemberCreditScoreService.java
├── MemberCreditScoreServiceImpl.java
├── MemberCreditLogService.java
└── MemberCreditLogServiceImpl.java
```

**核心方法**:
- `getCreditScore(Long userId)` - 获取用户信用分
- `addCredit(Long userId, Integer score, String reason)` - 增加信用分
- `deductCredit(Long userId, Integer score, String reason)` - 扣减信用分
- `recordDonate(Long userId, Long orderId)` - 记录捐赠(+10分)
- `recordReturnOnTime(Long userId, Long orderId)` - 记录按时归还(+5分)
- `recordOverdue(Long userId, Long orderId, Integer days)` - 记录逾期(-10分)
- `recordDamage(Long userId, Long orderId)` - 记录损坏(-20分)

#### 预约服务
```
yudao-module-mall/yudao-module-product/src/main/java/cn/iocoder/yudao/module/product/service/reservation/
├── EquipmentReservationService.java
└── EquipmentReservationServiceImpl.java
```

**核心方法**:
- `createReservation()` - 创建预约
- `approveReservation()` - 审核通过
- `rejectReservation()` - 审核拒绝
- `cancelReservation()` - 取消预约

### 第五步: 修改现有 Service

#### ProductSpuService
添加器材相关方法:
- `publishEquipment()` - 发布器材
- `updateEquipmentInfo()` - 更新器材信息
- `updateUsageCount()` - 更新使用次数
- `updateDisinfectionDate()` - 更新消毒时间

#### TradeOrderService
扩展借用流程:
- `createDonateOrder()` - 创建赠送订单
- `createBorrowOrder()` - 创建借用订单
- `confirmBorrow()` - 确认借出
- `requestReturn()` - 申请归还
- `confirmReturn()` - 确认归还
- `handleOverdue()` - 处理逾期
- `refundDeposit()` - 退还押金

### 第六步: 创建 Controller

```
会员端 API (app):
- AppEquipmentController - 器材浏览、申请
- AppDonateController - 我的赠送
- AppBorrowController - 我的借用
- AppCreditController - 我的信用分

管理端 API (admin):
- EquipmentController - 器材管理
- DonateOrderController - 赠送订单管理
- BorrowOrderController - 借用订单管理
- CreditManagementController - 信用分管理
- ReservationController - 预约审核
```

---

## 🎨 前端改造

### 菜单结构

```
器材共享 (/equipment)
├── 器材管理 (/equipment/item)
├── 赠送管理 (/equipment/donate)
├── 借用管理 (/equipment/borrow)
├── 预约审核 (/equipment/reservation)
├── 信用管理 (/equipment/credit)
└── 统计分析 (/equipment/statistics)
```

### 需要修改的文件

#### 1. 器材管理页面
**文件**: `/Users/admin/ma jia/yudao-ui-admin-vue3/src/views/mall/product/spu/`

**修改内容**:
- `index.vue` - 列表页，添加器材状态、共享类型等列
- `form/InfoForm.vue` - 表单页，添加器材特有字段
- 新建 `form/ShareForm.vue` - 共享设置表单

**新增字段**:
```vue
<el-form-item label="器材状态">
  <el-select v-model="formData.equipmentStatus">
    <el-option label="全新" :value="1" />
    <el-option label="九成新" :value="2" />
    <el-option label="八成新" :value="3" />
    <el-option label="七成新以下" :value="4" />
  </el-select>
</el-form-item>

<el-form-item label="适用年龄段">
  <el-input v-model="formData.suitableAgeRange" placeholder="例如: 3-6岁" />
</el-form-item>

<el-form-item label="训练类型">
  <el-checkbox-group v-model="formData.trainingTypes">
    <el-checkbox label="sensory">感统训练</el-checkbox>
    <el-checkbox label="language">语言训练</el-checkbox>
    <el-checkbox label="cognitive">认知训练</el-checkbox>
    <el-checkbox label="social">社交训练</el-checkbox>
    <el-checkbox label="fine_motor">精细动作</el-checkbox>
    <el-checkbox label="gross_motor">大运动</el-checkbox>
  </el-checkbox-group>
</el-form-item>

<el-form-item label="共享类型">
  <el-radio-group v-model="formData.shareType">
    <el-radio :label="1">仅赠送</el-radio>
    <el-radio :label="2">仅借用</el-radio>
    <el-radio :label="3">赠送或借用</el-radio>
  </el-radio-group>
</el-form-item>

<el-form-item label="押金金额" v-if="formData.shareType !== 1">
  <el-input-number v-model="formData.depositAmount" :min="0" />
  <span class="ml-2">元</span>
</el-form-item>

<el-form-item label="建议借用天数" v-if="formData.shareType !== 1">
  <el-input-number v-model="formData.borrowDuration" :min="1" :max="90" />
  <span class="ml-2">天</span>
</el-form-item>
```

#### 2. 订单管理页面
**文件**: `/Users/admin/ma jia/yudao-ui-admin-vue3/src/views/mall/trade/order/`

**修改内容**:
- `index.vue` - 添加订单类型筛选(赠送/借用)
- `detail/index.vue` - 详情页增加借用信息展示
- 新建 `components/ReturnDialog.vue` - 归还确认弹窗
- 新建 `components/DepositRefundDialog.vue` - 押金退还弹窗

#### 3. 创建信用管理页面
**新建目录**: `/Users/admin/ma jia/yudao-ui-admin-vue3/src/views/member/credit/`

```
credit/
├── index.vue - 信用分列表
├── CreditDetailDialog.vue - 信用分详情
└── CreditLogList.vue - 信用分变动记录
```

**核心功能**:
- 显示用户列表及信用分
- 查看信用分变动历史
- 手动调整信用分(管理员)
- 信用分规则说明

#### 4. 创建预约管理页面
**新建目录**: `/Users/admin/ma jia/yudao-ui-admin-vue3/src/views/mall/reservation/`

```
reservation/
├── index.vue - 预约列表
└── ApprovalDialog.vue - 审核弹窗
```

### API 接口文件

#### 创建新的 API 文件

```typescript
// src/api/mall/equipment/index.ts
export interface EquipmentVO {
  id: number
  name: string
  equipmentStatus: number
  suitableAgeRange: string
  trainingTypes: string
  shareType: number
  depositAmount: number
  ownerUserId: number
  // ... 其他字段
}

export const getEquipmentPage = (params: any) => {
  return request.get({ url: '/product/spu/page', params })
}

export const createEquipment = (data: EquipmentVO) => {
  return request.post({ url: '/product/spu/create', data })
}

// ... 其他接口

// src/api/member/credit/index.ts
export interface CreditScoreVO {
  id: number
  userId: number
  totalScore: number
  donateCount: number
  borrowCount: number
  overdueCount: number
}

export const getCreditScore = (userId: number) => {
  return request.get({ url: `/member/credit/score/${userId}` })
}

export const getCreditLogPage = (params: any) => {
  return request.get({ url: '/member/credit/log/page', params })
}
```

### 路由配置

**文件**: `/Users/admin/ma jia/yudao-ui-admin-vue3/src/router/modules/mall.ts`

```typescript
{
  path: '/equipment',
  component: Layout,
  name: 'Equipment',
  meta: {
    title: '器材共享',
    icon: 'ep:box'
  },
  children: [
    {
      path: 'item',
      component: () => import('@/views/mall/product/spu/index.vue'),
      name: 'EquipmentItem',
      meta: { title: '器材管理', icon: 'ep:goods' }
    },
    {
      path: 'donate',
      component: () => import('@/views/mall/trade/order/index.vue'),
      name: 'EquipmentDonate',
      meta: { title: '赠送管理', icon: 'ep:present' }
    },
    {
      path: 'borrow',
      component: () => import('@/views/mall/trade/order/index.vue'),
      name: 'EquipmentBorrow',
      meta: { title: '借用管理', icon: 'ep:refresh-left' }
    },
    {
      path: 'credit',
      component: () => import('@/views/member/credit/index.vue'),
      name: 'MemberCredit',
      meta: { title: '信用管理', icon: 'ep:medal' }
    }
  ]
}
```

---

## ⚙️ 配置修改

### 1. 字典配置

在系统管理 → 字典管理中添加以下字典类型:

| 字典类型 | 字典名称 | 备注 |
|----------|----------|------|
| `equipment_status` | 器材状态 | 1-全新, 2-九成新, 3-八成新, 4-七成新以下 |
| `share_type` | 共享类型 | 1-仅赠送, 2-仅借用, 3-赠送或借用 |
| `training_type` | 训练类型 | 感统、语言、认知、社交、精细动作、大运动 |
| `return_status` | 归还状态 | 0-未归还, 1-已归还, 2-逾期, 3-已损坏 |
| `credit_change_type` | 信用分变动类型 | 1-捐赠, 2-按时归还, 3-逾期, 4-损坏, 5-违规 |

### 2. 权限配置

在系统管理 → 菜单管理中配置权限:

```
equipment:item:query - 查询器材
equipment:item:create - 创建器材
equipment:item:update - 更新器材
equipment:item:delete - 删除器材

equipment:donate:query - 查询赠送订单
equipment:donate:approve - 审核赠送

equipment:borrow:query - 查询借用订单
equipment:borrow:approve - 审核借用
equipment:borrow:return - 确认归还
equipment:borrow:refund - 退还押金

member:credit:query - 查询信用分
member:credit:update - 调整信用分
```

---

## 📱 定时任务

需要创建的定时任务:

### 1. 逾期检查任务
**类名**: `BorrowOverdueCheckJob.java`  
**执行周期**: 每天凌晨 1:00  
**功能**: 检查借用订单，标记逾期，扣除信用分

```java
@TenantJob
@Component
public class BorrowOverdueCheckJob implements JobHandler {
    @Override
    public String execute(String param) {
        // 查询所有未归还且已过期的订单
        // 更新订单状态为逾期
        // 扣除用户信用分
        // 发送提醒通知
    }
}
```

### 2. 预约过期清理任务
**类名**: `ReservationExpireCleanJob.java`  
**执行周期**: 每天凌晨 2:00  
**功能**: 清理过期未确认的预约

### 3. 押金退还提醒任务
**类名**: `DepositRefundRemindJob.java`  
**执行周期**: 每天上午 10:00  
**功能**: 提醒管理员处理待退还押金

---

## 🧪 测试清单

### 功能测试

- [ ] 器材发布与编辑
- [ ] 赠送流程完整测试
- [ ] 借用流程完整测试
- [ ] 归还流程测试(正常/逾期/损坏)
- [ ] 押金支付与退还
- [ ] 信用分自动计算
- [ ] 预约审核流程
- [ ] 逾期处理逻辑
- [ ] 定时任务执行

### 界面测试

- [ ] 器材列表展示
- [ ] 器材详情页
- [ ] 表单验证
- [ ] 筛选搜索功能
- [ ] 状态标签显示
- [ ] 响应式布局

### 性能测试

- [ ] 列表分页加载
- [ ] 大数据量查询
- [ ] 并发订单处理

---

## 📝 部署步骤

### 1. 数据库升级

```bash
# 1. 备份数据库
./backup.sh

# 2. 执行改造脚本
psql -f sql/postgresql/equipment_renovation.sql

# 3. 验证数据结构
psql -c "\d+ product_spu"
```

### 2. 后端部署

```bash
# 1. 拉取最新代码
git pull

# 2. 编译打包
mvn clean package -DskipTests

# 3. 重启服务
./deploy.sh restart
```

### 3. 前端部署

```bash
# 1. 安装依赖
cd yudao-ui-admin-vue3
pnpm install

# 2. 构建生产版本
pnpm build:prod

# 3. 部署到服务器
scp -r dist/* user@server:/var/www/html/
```

### 4. 验证部署

- [ ] 登录后台管理系统
- [ ] 检查新菜单是否显示
- [ ] 创建测试器材
- [ ] 创建测试订单
- [ ] 检查信用分功能

---

## 🔍 常见问题

### Q1: 数据库脚本执行失败？

**A**: 检查以下几点:
1. 数据库版本是否兼容(PostgreSQL >= 12, MySQL >= 5.7)
2. 是否有足够的权限
3. 表名是否冲突
4. 字段是否已存在(可删除 `IF NOT EXISTS` 重新执行)

### Q2: 菜单不显示？

**A**: 
1. 检查菜单的 `parent_id` 是否正确
2. 清除浏览器缓存
3. 检查用户是否有权限
4. 查看后台菜单管理是否正确配置

### Q3: 信用分不自动更新？

**A**:
1. 检查定时任务是否启动
2. 查看 Service 层方法是否正确调用
3. 检查事务是否正常提交

### Q4: 借用订单状态异常？

**A**:
1. 检查订单状态机配置
2. 查看日志文件定位问题
3. 手动修正异常数据

---

## 📚 扩展功能建议

### 短期(1-2个月)

- [ ] 手机端 H5 页面
- [ ] 微信小程序
- [ ] 消息通知(站内信、短信、邮件)
- [ ] 器材评价系统
- [ ] 用户间私信功能

### 中期(3-6个月)

- [ ] 地图定位与就近匹配
- [ ] 在线支付集成
- [ ] 物流追踪
- [ ] 智能推荐系统
- [ ] 数据统计大屏

### 长期(6个月+)

- [ ] AI 智能匹配
- [ ] 区块链溯源
- [ ] 社区论坛
- [ ] 康复知识库
- [ ] 专家在线咨询

---

## 👥 团队分工建议

| 角色 | 职责 | 预计工时 |
|------|------|----------|
| 后端开发 | 数据库设计、API开发、业务逻辑 | 80h |
| 前端开发 | 页面开发、交互优化 | 60h |
| 测试工程师 | 功能测试、性能测试 | 40h |
| 产品经理 | 需求梳理、原型设计 | 20h |
| 项目经理 | 进度管控、资源协调 | 全程 |

---

## 📞 技术支持

如有问题，请联系:

- **技术负责人**: [您的名字]
- **Email**: [您的邮箱]
- **项目地址**: https://github.com/your-repo
- **文档地址**: https://your-docs-url

---

## 📄 更新日志

### v1.0.0 (2025-12-15)
- ✅ 完成数据库改造脚本(PostgreSQL + MySQL)
- ✅ 创建改造指南文档
- 🔄 进行中: 后端代码改造
- ⏳ 待开始: 前端页面改造

---

**改造完成后，您将拥有一个完整的康复训练器材共享平台！** 🎉

