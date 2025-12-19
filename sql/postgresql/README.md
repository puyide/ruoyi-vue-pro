# PostgreSQL SQL脚本执行指南

---

## ⚡ 最快执行方式（推荐）

在DataGrip中依次执行以下4个文件：

| # | 文件 | 说明 | 时间 |
|---|------|------|------|
| 1️⃣ | `mall_base_ddl.sql` | 创建49个商城表 | ~5秒 |
| 2️⃣ | `mall_base_dml.sql` | 插入商城数据 | ~1分钟 |
| 3️⃣ | `equipment_renovation_standalone.sql` ⭐ | 器材共享改造 | ~10秒 |
| 4️⃣ | `equipment_handover_standalone.sql` ⭐ | 交接会话功能 | ~10秒 |

**特点**: 不依赖system_dict_type表，可立即执行！

---

## 📁 文件说明

### 商城基础（必须）

- **mall_base_ddl.sql** (38KB) - 表结构
- **mall_base_dml.sql** (813KB) - 数据
- **mall_base_final.sql** (850KB) - 完整版（DDL+DML合并）

### 器材共享改造

- **equipment_renovation.sql** (18KB) - 完整版（需要system表）
- **equipment_renovation_standalone.sql** (9.2KB) ⭐ - 独立版（不需要system表）

### 交接会话功能

- **equipment_handover.sql** (18KB) - 完整版（需要system表）
- **equipment_handover_standalone.sql** (11KB) ⭐ - 独立版（不需要system表）

### 其他文件

- **ruoyi-vue-pro.sql** (892KB) - 系统基础表（如果需要完整system表）

---

## 🎯 执行策略

### 策略A: 快速开始（不需要system表）

```
mall_base_ddl.sql
mall_base_dml.sql
equipment_renovation_standalone.sql
equipment_handover_standalone.sql
```
✅ 可立即执行  
⚠️ 数据字典需后续手动配置

### 策略B: 完整部署（需要system表）

```
ruoyi-vue-pro.sql
mall_base_ddl.sql
mall_base_dml.sql
equipment_renovation.sql
equipment_handover.sql
```
✅ 包含完整数据字典  
⚠️ 需要先有系统基础表

---

## 📋 验证

```sql
-- 检查表数量
\dt

-- 检查商城表
SELECT COUNT(*) FROM product_brand;

-- 检查器材共享表
SELECT COUNT(*) FROM member_credit_score;

-- 检查交接会话表
SELECT COUNT(*) FROM equipment_handover;
```

---

## 📚 详细文档

- **🚀_START.md** - 超简洁指南
- **🎯_最简执行.txt** - 快速参考
- **⭐️_完整部署指南.md** - 详细说明
- **🔥_快速解决方案.md** - 问题解决

---

<p align="center">
  <b>立即在DataGrip中执行这4个文件！</b><br>
  <br>
  mall_base_ddl.sql<br>
  mall_base_dml.sql<br>
  equipment_renovation_standalone.sql ⭐<br>
  equipment_handover_standalone.sql ⭐<br>
</p>

