#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MySQL to PostgreSQL SQL Converter - Advanced Version
高级MySQL到PostgreSQL转换工具
"""

import re
import sys
from pathlib import Path

def convert_mysql_to_postgresql(mysql_sql):
    """
    将MySQL SQL语句转换为PostgreSQL兼容的SQL语句
    """
    sql = mysql_sql
    
    print("开始转换...")
    
    # 1. 移除MySQL特有的SET语句
    print("  移除SET语句...")
    sql = re.sub(r'SET\s+NAMES\s+\w+\s*;', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET\s+FOREIGN_KEY_CHECKS\s*=\s*\d+\s*;', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET\s+SQL_MODE\s*=\s*[^;]+;', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET\s+TIME_ZONE\s*=\s*[^;]+;', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET\s+AUTOCOMMIT\s*=\s*\d+\s*;', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'SET\s+@\w+\s*=\s*[^;]+;', '', sql, flags=re.IGNORECASE)
    
    # 2. 移除版本特定注释
    print("  移除版本特定注释...")
    sql = re.sub(r'/\*!40\d{3}.*?\*/', '', sql, flags=re.DOTALL)
    sql = re.sub(r'/\*!50\d{3}.*?\*/', '', sql, flags=re.DOTALL)
    
    # 3. 移除LOCK/UNLOCK TABLES
    print("  移除LOCK/UNLOCK语句...")
    sql = re.sub(r'LOCK\s+TABLES.*?;', '', sql, flags=re.IGNORECASE | re.DOTALL)
    sql = re.sub(r'UNLOCK\s+TABLES\s*;', '', sql, flags=re.IGNORECASE)
    
    # 4. 移除MySQL特有的反引号
    print("  移除反引号...")
    sql = sql.replace('`', '')
    
    # 5. 转换AUTO_INCREMENT为SERIAL/BIGSERIAL
    print("  转换AUTO_INCREMENT...")
    # 必须先处理带NOT NULL的情况
    sql = re.sub(r'\bbigint\s+NOT\s+NULL\s+AUTO_INCREMENT\b', 'BIGSERIAL', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bint\s+NOT\s+NULL\s+AUTO_INCREMENT\b', 'SERIAL', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bbigint\s+AUTO_INCREMENT\b', 'BIGSERIAL', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bint\s+AUTO_INCREMENT\b', 'SERIAL', sql, flags=re.IGNORECASE)
    
    # 6. 转换数据类型
    print("  转换数据类型...")
    sql = re.sub(r'\btinyint\b', 'SMALLINT', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bdatetime\b', 'TIMESTAMP', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\blongtext\b', 'TEXT', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bmediumtext\b', 'TEXT', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\btinytext\b', 'TEXT', sql, flags=re.IGNORECASE)
    sql = re.sub(r'\bdouble\b', 'DOUBLE PRECISION', sql, flags=re.IGNORECASE)
    
    # BIT类型转换
    sql = re.sub(r"bit\(1\)", 'BOOLEAN', sql, flags=re.IGNORECASE)
    sql = re.sub(r"\bbit\b", 'BOOLEAN', sql, flags=re.IGNORECASE)
    
    # 7. 转换字符集和排序规则
    print("  移除字符集声明...")
    sql = re.sub(r'CHARACTER\s+SET\s+\w+', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'COLLATE\s+\w+', '', sql, flags=re.IGNORECASE)
    
    # 8. 转换引擎和其他MySQL特有选项
    print("  移除ENGINE声明...")
    sql = re.sub(r'ENGINE\s*=\s*InnoDB\s*', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'DEFAULT\s+CHARSET\s*=\s*\w+\s*', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'AUTO_INCREMENT\s*=\s*\d+\s*', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'ROW_FORMAT\s*=\s*\w+\s*', '', sql, flags=re.IGNORECASE)
    
    # 9. 转换DEFAULT值
    print("  转换DEFAULT值...")
    sql = re.sub(r"DEFAULT\s+b'0'", "DEFAULT FALSE", sql, flags=re.IGNORECASE)
    sql = re.sub(r"DEFAULT\s+b'1'", "DEFAULT TRUE", sql, flags=re.IGNORECASE)
    sql = re.sub(r'ON\s+UPDATE\s+CURRENT_TIMESTAMP', '', sql, flags=re.IGNORECASE)
    
    # 10. 转换UNSIGNED (PostgreSQL不支持UNSIGNED)
    print("  移除UNSIGNED...")
    sql = re.sub(r'\bUNSIGNED\b', '', sql, flags=re.IGNORECASE)
    
    # 11. 转换PRIMARY KEY语法
    print("  修正PRIMARY KEY...")
    sql = re.sub(r'PRIMARY\s+INDEX', 'PRIMARY KEY', sql, flags=re.IGNORECASE)
    
    # 12. 转换索引语法
    print("  转换索引语法...")
    sql = re.sub(r'\bKEY\s+', '', sql, flags=re.IGNORECASE)  # 移除KEY关键字，保留索引名
    sql = re.sub(r'UNIQUE\s+KEY\s+', 'UNIQUE ', sql, flags=re.IGNORECASE)
    sql = re.sub(r'USING\s+BTREE', '', sql, flags=re.IGNORECASE)
    sql = re.sub(r'USING\s+HASH', '', sql, flags=re.IGNORECASE)
    
    # 13. 处理JSON类型
    sql = re.sub(r'\bjson\b', 'JSONB', sql, flags=re.IGNORECASE)
    
    # 14. 移除表级别的COMMENT (需要转换为COMMENT ON语法)
    print("  处理COMMENT语法...")
    sql = re.sub(r'\)\s*COMMENT\s*=\s*[\'"]([^\'"]+)[\'"];', r');', sql, flags=re.IGNORECASE)
    
    # 15. 移除列级别的COMMENT (可选：保留或移除)
    # 注意：列的COMMENT在PostgreSQL中需要用COMMENT ON COLUMN语法，这里先移除
    sql = re.sub(r"\s+COMMENT\s+['\"]([^'\"]+)['\"]", '', sql, flags=re.IGNORECASE)
    
    # 16. 清理多余的逗号和空行
    print("  清理格式...")
    sql = re.sub(r',(\s*)\)', r'\1)', sql)  # 移除结尾多余的逗号
    sql = re.sub(r'\n\s*\n\s*\n+', '\n\n', sql)  # 压缩多个空行
    sql = re.sub(r'  +', ' ', sql)  # 压缩多个空格
    
    # 17. 移除BEGIN/COMMIT (让用户自己决定是否使用事务)
    sql = re.sub(r'^BEGIN\s*;', '', sql, flags=re.MULTILINE)
    sql = re.sub(r'COMMIT\s*;', '', sql, flags=re.MULTILINE)
    
    return sql

def convert_file(input_file, output_file):
    """
    转换整个SQL文件
    """
    print("="*60)
    print(f"MySQL to PostgreSQL 转换工具 v2.0")
    print("="*60)
    print(f"输入文件: {input_file}")
    print(f"输出文件: {output_file}")
    print("="*60)
    
    try:
        # 读取MySQL SQL文件
        with open(input_file, 'r', encoding='utf-8', errors='ignore') as f:
            mysql_sql = f.read()
        
        print(f"文件大小: {len(mysql_sql):,} 字节")
        print(f"行数: {mysql_sql.count(chr(10)):,} 行")
        print("="*60)
        
        # 转换
        postgresql_sql = convert_mysql_to_postgresql(mysql_sql)
        
        # 添加PostgreSQL特有的头部
        header = """-- ============================================================
-- PostgreSQL SQL Script
-- 自动转换自MySQL脚本
-- 转换日期: 2025-12-15
-- 转换工具: mysql_to_pg_advanced.py v2.0
-- ============================================================
-- 使用说明:
-- 1. 本脚本已移除MySQL特有语法
-- 2. COMMENT已被移除(如需要可手动添加COMMENT ON语句)
-- 3. 索引已简化(基本索引应该正常工作)
-- 4. 建议先在测试环境执行
-- ============================================================

-- 设置客户端编码
SET client_encoding = 'UTF8';

"""
        
        footer = """

-- ============================================================
-- 转换完成
-- ============================================================
"""
        
        postgresql_sql = header + postgresql_sql + footer
        
        # 写入PostgreSQL SQL文件
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(postgresql_sql)
        
        print("="*60)
        print(f"✓ 转换完成！")
        print(f"✓ 输出文件: {output_file}")
        print(f"✓ 输出大小: {len(postgresql_sql):,} 字节")
        print(f"✓ 输出行数: {postgresql_sql.count(chr(10)):,} 行")
        print("="*60)
        print("\n下一步:")
        print("1. 执行: psql -U user -d database -f", output_file)
        print("2. 如遇到错误，查看 EXECUTE_POSTGRESQL_SQL.md")
        print("3. 验证: psql -d database -c \"\\dt\"")
        print("="*60)
        
        return True
        
    except Exception as e:
        print(f"✗ 转换失败: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("用法: python3 mysql_to_pg_advanced.py <input_mysql.sql> [output_postgresql.sql]")
        print("示例: python3 mysql_to_pg_advanced.py mall.sql mall_pg.sql")
        sys.exit(1)
    
    input_file = sys.argv[1]
    
    if len(sys.argv) >= 3:
        output_file = sys.argv[2]
    else:
        # 自动生成输出文件名
        input_path = Path(input_file)
        output_file = input_path.parent / f"{input_path.stem}_pg.sql"
    
    # 检查输入文件是否存在
    if not Path(input_file).exists():
        print(f"✗ 错误: 输入文件不存在: {input_file}")
        sys.exit(1)
    
    # 执行转换
    success = convert_file(input_file, output_file)
    
    sys.exit(0 if success else 1)

if __name__ == '__main__':
    main()

