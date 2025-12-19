# 🔴 重要提示：执行 standalone 版本！

---

## ⚠️ 您刚才执行错了！

### ❌ 错误的文件（不要执行）

- `equipment_renovation.sql`  
- `equipment_handover.sql`

这两个文件需要 system_dict_type 表！

---

### ✅ 正确的文件（执行这些）

- `equipment_renovation_standalone.sql` ⭐⭐⭐
- `equipment_handover_standalone.sql` ⭐⭐⭐

带 **_standalone** 后缀的是独立版本！

---

## 🚀 正确的执行顺序

在DataGrip中依次执行：

```
1. mall_base_ddl.sql
2. mall_base_dml.sql
3. equipment_renovation_standalone.sql      ← 注意：standalone
4. equipment_handover_standalone.sql        ← 注意：standalone
```

---

## 📝 文件名对照表

| ❌ 不要执行 | ✅ 执行这个 |
|-----------|----------|
| equipment_renovation.sql | equipment_renovation_standalone.sql |
| equipment_handover.sql | equipment_handover_standalone.sql |

---

<p align="center">
  <b>🔴 请执行带 _standalone 后缀的文件！</b><br>
  <br>
  <code>equipment_renovation_standalone.sql</code> ⭐<br>
  <code>equipment_handover_standalone.sql</code> ⭐<br>
</p>

