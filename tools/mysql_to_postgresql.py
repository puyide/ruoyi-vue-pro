#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MySQL to PostgreSQL SQL Converter
将MySQL SQL脚本转换为PostgreSQL兼容的SQL脚本
"""

import re
import sys
from pathlib import Path

def convert_mysql_to_postgresql(mysql_sql):
    """
    将MySQL SQL语句转换为PostgreSQL兼容的SQL语句
    """
    sql = mysql_sql
    
    # 1. 移除MySQL特有的反引号
    sql = sql.replace('`', '')
    
    # 2. 转换AUTO_INCREMENT为SERIAL/BIGSERIAL
    sql = re.sub(r'\bbigint\s+NOT\s+NULL\s+AUTO_INCREMENT\b', 'BIGSERIAL', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bint\s+NOT\s+NULL\s+AUTO_INCREMENT\b', 'SERIAL', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bbigint\s+AUTO_INCREMENT\b', 'BIGSERIAL', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bint\s+AUTO_INCREMENT\b', 'SERIAL', sql, flags=re.IGNORECASE)
    
    # 3. 转换数据类型
    sql = re.sub(r'\btinyint\b', 'SMALLINT', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bdatetime\b', 'TIMESTAMP', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\blongtext\b', 'TEXT', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bmediumtext\b', 'TEXT', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\btinytext\b', 'TEXT', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bdouble\b', 'DOUBLE PRECISION', sql, flags=re.IGNORECASE)
    
    # BIT类型转换
    sql = re.sub(r"bit\(1\)", 'BOOLEAN', sql, flags=re.IGNORECASE)
    sql = re.sub(r"\bbit\b", 'BOOLEAN', sql, flags=re.IGNORECASE)
    
    # 4. 转换字符集和排序规则
    sql = re.sub(r'CHARACTER SET \w+', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'COLLATE \w+', '', sql, flags=re.IGNORECASE)
    
    # 5. 转换引擎和其他MySQL特有选项
    sql = re.sub(r'ENGINE\s*=\s*InnoDB\s*', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'DEFAULT CHARSET\s*=\s*\w+\s*', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'AUTO_INCREMENT\s*=\s*\d+\s*', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'ROW_FORMAT\s*=\s*\w+\s*', '', sql, flags=re.IGNORECASE)
    
    # 6. 转换DEFAULT值
    sql = re.sub(r"DEFAULT b'0'", "DEFAULT FALSE", sql)
    sql = re.sub(r"DEFAULT b'1'", "DEFAULT TRUE", sql)
    sql = re.sub(r'ON UPDATE CURRENT_TIMESTAMP', '', sql, flags=re.IGNORECASE)
    
    # 7. 转换UNSIGNED (PostgreSQL不支持UNSIGNED)
    sql = re.sub(r'\bUNSIGNED\b', '', sql, flags=re.IGNORECASE)
    
    # 8. 转换索引语法 (KEY -> INDEX)
    sql = re.sub(r'\bKEY\s+', 'INDEX ', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bUNIQUE KEY\s+', 'UNIQUE INDEX ', sql, flags=re.IGNORECASE)
    
    # 9. 转换USING BTREE/HASH
    sql = re.sub(r'USING BTREE', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'USING HASH', '', sql, flags=re.IGNORECASE)
    
    # 10. 处理JSON类型 (MySQL的json -> PostgreSQL的jsonb)
    sql = re.sub(r'\bjson\b', 'JSONB', sql, flags=re.IGNORECASE)
    
    # 11. 移除MySQL特有的SET语句
    sql = re.sub(r'SET NAMES \w+;?', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET FOREIGN_KEY_CHECKS\s*=\s*\d+;?', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET SQL_MODE\s*=\s*[^;]+;?', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET TIME_ZONE\s*=\s*[^;]+;?', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET AUTOCOMMIT\s*=\s*\d+;?', '', sql, flags=re.IGNORECASE)
    
    # 12. 移除MySQL特有的注释
    sql = re.sub(r'/\*!40\d{3}.*?\*/', '', sql, flags=re.DOTALL)
    
    # 13. 移除LOCK/UNLOCK TABLES
    sql = re.sub(r'LOCK TABLES.*?;', '', sql, flags=re.IGNORECASE | re.DOTALL)
    sql = re.sub(r'UNLOCK TABLES;?', '', sql, flags=re.IGNORECASE)
    
    # 14. 清理多余的空格和逗号
    sql = re.sub(r',\s*\)', ')', sql)  # 移除结尾多余的逗号
    sql = re.sub(r'\n\s*\n\s*\n', '\n\n', sql)  # 压缩多个空行
    
    return sql

def convert_file(input_file, output_file):
    """
    转换整个SQL文件
    """
    print(f"开始转换: {input_file}")
    print(f"输出文件: {output_file}")
    
    try:
        # 读取MySQL SQL文件
        with open(input_file, 'r', encoding='utf-8') as f:
            mysql_sql = f.read()
        
        print(f"文件大小: {len(mysql_sql)} 字节")
        
        # 转换
        postgresql_sql = convert_mysql_to_postgresql(mysql_sql)
        
        # 添加PostgreSQL特有的头部
        header = """-- ----------------------------
-- PostgreSQL SQL Script
-- Converted from MySQL
-- Conversion Date: 2025-12-15
-- ----------------------------

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- 开始事务
BEGIN;

"""
        
        footer = """
-- 提交事务
COMMIT;

-- 完成
"""
        
        postgresql_sql = header + postgresql_sql + footer
        
        # 写入PostgreSQL SQL文件
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(postgresql_sql)
        
        print(f"✓ 转换完成！")
        print(f"✓ 输出文件: {output_file}")
        print(f"✓ 输出大小: {len(postgresql_sql)} 字节")
        
        return True
        
    except Exception as e:
        print(f"✗ 转换失败: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("用法: python3 mysql_to_postgresql.py <input_mysql.sql> [output_postgresql.sql]")
        print("示例: python3 mysql_to_postgresql.py mall.sql mall_pg.sql")
        sys.exit(1)
    
    input_file = sys.argv[1]
    
    if len(sys.argv) >= 3:
        output_file = sys.argv[2]
    else:
        # 自动生成输出文件名
        input_path = Path(input_file)
        output_file = input_path.parent / f"{input_path.stem}_postgresql.sql"
    
    # 检查输入文件是否存在
    if not Path(input_file).exists():
        print(f"✗ 错误: 输入文件不存在: {input_file}")
        sys.exit(1)
    
    # 执行转换
    success = convert_file(input_file, output_file)
    
    if success:
        print("\n" + "="*50)
        print("转换成功！")
        print("="*50)
        print(f"\n下一步:")
        print(f"1. 检查输出文件: {output_file}")
        print(f"2. 手动调整COMMENT语法 (PostgreSQL使用 COMMENT ON 语句)")
        print(f"3. 检查并调整索引定义")
        print(f"4. 执行: psql -U user -d database -f {output_file}")
        sys.exit(0)
    else:
        sys.exit(1)

if __name__ == '__main__':
    main()

