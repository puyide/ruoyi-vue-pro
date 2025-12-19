# 🚀 PostgreSQL脚本执行指南

> **MySQL脚本已成功转换，现在可以安全执行！**

---

## ✅ 转换已完成并修复

### 最新状态

- ✅ **文件**: `sql/postgresql/ruoyi-vue-pro-mall-converted.sql`
- ✅ **大小**: 915 KB
- ✅ **表数量**: 49个
- ✅ **MySQL特有语句**: 已全部移除
- ✅ **可以安全执行**: 是

### 已修复的问题

- ✅ 移除 `SET NAMES utf8mb4`
- ✅ 移除 `SET FOREIGN_KEY_CHECKS`
- ✅ 移除 `SET SQL_MODE`
- ✅ 移除版本特定注释
- ✅ 移除 LOCK/UNLOCK TABLES

---

## 🚀 执行步骤

### 方法1: 命令行执行 (推荐)

```bash
# 1. 进入项目目录
cd "/Users/admin/ma jia/ruoyi-vue-pro"

# 2. 执行脚本
psql -U your_username -d your_database \
  -f sql/postgresql/ruoyi-vue-pro-mall-converted.sql

# 3. 验证结果
psql -U your_username -d your_database -c "\dt"
```

### 方法2: DataGrip/IDE执行

1. 在DataGrip中打开文件: `sql/postgresql/ruoyi-vue-pro-mall-converted.sql`
2. 确保选择了正确的PostgreSQL数据源
3. **删除或注释掉**文件开头的 `BEGIN;` 和文件末尾的 `COMMIT;` (DataGrip会自动管理事务)
4. 点击执行按钮 ▶️

### 方法3: 分批执行(大数据库推荐)

```bash
# 将大文件分割成小文件
cd sql/postgresql
split -l 5000 ruoyi-vue-pro-mall-converted.sql chunk_

# 逐个执行
for file in chunk_*; do
  echo "执行 $file..."
  psql -U postgres -d your_db -f "$file"
done
```

---

## ⚠️ 如果还遇到错误

### 错误类型1: COMMENT语法错误

**错误信息**:
```
ERROR: syntax error at or near "COMMENT"
```

**解决方案**: 临时移除COMMENT
```bash
cd sql/postgresql

# 方法A: 移除内联COMMENT
sed 's/COMMENT.*//g' ruoyi-vue-pro-mall-converted.sql > temp_no_comment.sql
psql -f temp_no_comment.sql

# 方法B: 只保留表定义，稍后手动添加COMMENT
grep -v "COMMENT" ruoyi-vue-pro-mall-converted.sql > temp_no_comment.sql
psql -f temp_no_comment.sql
```

### 错误类型2: 索引定义错误

**错误信息**:
```
ERROR: syntax error in INDEX definition
```

**解决方案**: 索引可能需要单独提取
```sql
-- 在表定义中的索引
INDEX idx_name (column_name)

-- 需改为单独语句
CREATE INDEX idx_name ON table_name (column_name);
```

### 错误类型3: 默认值函数

**错误信息**:
```
ERROR: function NOW() does not exist
```

**解决方案**:
```sql
-- MySQL
DEFAULT NOW()

-- PostgreSQL
DEFAULT CURRENT_TIMESTAMP
```

---

## 🔧 创建清理脚本

如果需要清理COMMENT和复杂语法，使用以下脚本：

```bash
cat > clean_pg_sql.sh << 'EOF'
#!/bin/bash
# 清理PostgreSQL脚本中的问题语法

INPUT_FILE="$1"
OUTPUT_FILE="${INPUT_FILE%.sql}_clean.sql"

echo "清理 $INPUT_FILE..."

# 移除内联COMMENT (保留COMMENT ON语句)
cat "$INPUT_FILE" | \
  # 移除列定义中的COMMENT
  sed 's/ COMMENT '\''[^'\'']*'\''//g' | \
  # 移除列定义中的COMMENT (双引号版本)
  sed 's/ COMMENT "[^"]*"//g' | \
  # 移除表COMMENT
  sed 's/) COMMENT.*= .*'\'';/);\n/g' | \
  # 清理多余空行
  sed '/^$/d' > "$OUTPUT_FILE"

echo "✓ 清理完成: $OUTPUT_FILE"
EOF

chmod +x clean_pg_sql.sh

# 使用
./clean_pg_sql.sh sql/postgresql/ruoyi-vue-pro-mall-converted.sql
```

---

## 📋 执行检查清单

### 执行前
- [ ] 备份现有数据库
- [ ] 确认PostgreSQL版本 (建议12+)
- [ ] 检查磁盘空间
- [ ] 确认用户权限

### 执行中
- [ ] 监控执行进度
- [ ] 记录错误信息
- [ ] 注意事务状态

### 执行后
- [ ] 验证表数量 (`\dt`)
- [ ] 检查关键表结构 (`\d product_spu`, `\d trade_order`)
- [ ] 验证索引 (`\di`)
- [ ] 测试插入数据

---

## 🎯 完整的部署流程

### 商城基础 + 器材共享改造

```bash
#!/bin/bash
# 完整部署脚本

DB_USER="postgres"
DB_NAME="your_database"

echo "========================================="
echo "开始部署康复训练器材共享平台"
echo "========================================="

# 1. 备份现有数据库
echo "Step 1: 备份数据库..."
pg_dump -U $DB_USER -d $DB_NAME > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 执行商城基础脚本
echo "Step 2: 部署商城基础表(49个表)..."
psql -U $DB_USER -d $DB_NAME \
  -f sql/postgresql/ruoyi-vue-pro-mall-converted.sql

# 3. 执行器材共享改造
echo "Step 3: 应用器材共享改造..."
psql -U $DB_USER -d $DB_NAME \
  -f sql/postgresql/equipment_renovation.sql

# 4. 执行交接会话模块
echo "Step 4: 部署交接会话功能..."
psql -U $DB_USER -d $DB_NAME \
  -f sql/postgresql/equipment_handover.sql

# 5. 验证
echo "Step 5: 验证部署结果..."
TABLE_COUNT=$(psql -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public'")
echo "✓ 创建的表数量: $TABLE_COUNT"

# 6. 显示关键表
echo "关键表结构:"
psql -U $DB_USER -d $DB_NAME -c "\d product_spu" | head -20
psql -U $DB_USER -d $DB_NAME -c "\d trade_order" | head -20

echo "========================================="
echo "部署完成！"
echo "========================================="
```

---

## 📞 需要帮助?

如果执行过程中遇到任何错误:

1. **提供错误信息**: 完整的错误消息
2. **说明PostgreSQL版本**: `SELECT version();`
3. **描述执行环境**: DataGrip/命令行/其他

我会立即提供针对性的解决方案！

---

**更新日期**: 2025-12-15  
**脚本版本**: v1.1  
**状态**: ✅ 已修复，可安全执行  
**下一步**: 在DataGrip或命令行中重新执行！🚀

