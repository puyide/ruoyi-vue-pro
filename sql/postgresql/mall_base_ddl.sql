-- ============================================================
-- PostgreSQL SQL Script - DDL部分 (仅表结构)
-- 转换日期: 2025-12-15
-- 说明: 先执行此文件创建所有表
-- ============================================================

SET client_encoding = 'UTF8';

DROP TABLE IF EXISTS product_brand;

CREATE TABLE product_brand (
 id BIGSERIAL,
 name varchar(255) NOT NULL,
 pic_url varchar(255) NOT NULL,
 sort int NULL DEFAULT 0,
 description varchar(1024) NULL DEFAULT NULL,
 status SMALLINT NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_browse_history;

CREATE TABLE product_browse_history (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 spu_id bigint NOT NULL,
 user_deleted BOOLEAN NOT NULL DEFAULT FALSE,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_category;

CREATE TABLE product_category (
 id BIGSERIAL,
 parent_id bigint NOT NULL,
 name varchar(255) NOT NULL,
 pic_url varchar(255) NOT NULL,
 sort int NULL DEFAULT 0,
 status SMALLINT NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_comment;

CREATE TABLE product_comment (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 user_nickname varchar(255) NULL DEFAULT NULL,
 user_avatar varchar(1024) NULL DEFAULT NULL,
 anonymous BOOLEAN NOT NULL,
 order_id bigint NULL DEFAULT 0,
 order_item_id bigint NULL DEFAULT 0,
 spu_id bigint NOT NULL,
 spu_name varchar(255) NULL DEFAULT NULL,
 sku_id bigint NOT NULL,
 sku_pic_url varchar(256) NOT NULL,
 sku_properties varchar(512) NULL DEFAULT NULL,
 visible BOOLEAN NULL DEFAULT NULL,
 scores SMALLINT NOT NULL,
 description_scores SMALLINT NOT NULL,
 benefit_scores SMALLINT NOT NULL,
 content varchar(1024) NOT NULL,
 pic_urls varchar(4096) NULL DEFAULT NULL,
 reply_status BOOLEAN NULL DEFAULT FALSE,
 reply_user_id bigint NULL DEFAULT NULL,
 reply_content varchar(1024) NULL DEFAULT NULL,
 reply_time TIMESTAMP NULL DEFAULT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_favorite;

CREATE TABLE product_favorite (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 spu_id bigint NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_property;

CREATE TABLE product_property (
 id BIGSERIAL,
 name varchar(64) NULL DEFAULT NULL,
 status SMALLINT NULL DEFAULT NULL,
 remark varchar(128) NULL DEFAULT NULL,
 create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 creator varchar(64) NULL DEFAULT NULL,
 updater varchar(64) NULL DEFAULT NULL,
 tenant_id bigint NOT NULL DEFAULT 0,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_property_value;

CREATE TABLE product_property_value (
 id BIGSERIAL,
 property_id bigint NULL DEFAULT NULL,
 name varchar(128) NULL DEFAULT NULL,
 status SMALLINT NULL DEFAULT NULL,
 remark varchar(128) NULL DEFAULT NULL,
 create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 creator varchar(64) NULL DEFAULT NULL,
 updater varchar(64) NULL DEFAULT NULL,
 tenant_id bigint NOT NULL DEFAULT 0,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_sku;

CREATE TABLE product_sku (
 id BIGSERIAL,
 spu_id bigint NOT NULL,
 properties varchar(512) NULL DEFAULT NULL,
 price int NOT NULL DEFAULT -1,
 market_price int NULL DEFAULT NULL,
 cost_price int NOT NULL DEFAULT -1,
 bar_code varchar(64) NULL DEFAULT NULL,
 pic_url varchar(256) NOT NULL,
 stock int NULL DEFAULT NULL,
 weight DOUBLE PRECISION NULL DEFAULT NULL,
 volume DOUBLE PRECISION NULL DEFAULT NULL,
 first_brokerage_price int NULL DEFAULT NULL,
 second_brokerage_price int NULL DEFAULT NULL,
 sales_count int NULL DEFAULT NULL,
 create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 creator varchar(64) NULL DEFAULT NULL,
 updater varchar(64) NULL DEFAULT NULL,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_spu;

CREATE TABLE product_spu (
 id BIGSERIAL,
 name varchar(128) NOT NULL,
 keyword varchar(256) NULL DEFAULT NULL,
 introduction varchar(256) NULL DEFAULT NULL,
 description text NULL,
 category_id bigint NOT NULL,
 brand_id int NULL DEFAULT NULL,
 pic_url varchar(256) NOT NULL,
 slider_pic_urls varchar(2000) NULL DEFAULT '',
 sort int NOT NULL DEFAULT 0,
 status SMALLINT NOT NULL,
 spec_type BOOLEAN NULL DEFAULT NULL,
 price int NOT NULL DEFAULT -1,
 market_price int NULL DEFAULT NULL,
 cost_price int NOT NULL DEFAULT -1,
 stock int NOT NULL DEFAULT 0,
 delivery_types varchar(32) NOT NULL DEFAULT '',
 delivery_template_id bigint NULL DEFAULT NULL,
 give_integral int NOT NULL DEFAULT 0,
 sub_commission_type BOOLEAN NULL DEFAULT NULL,
 sales_count int NULL DEFAULT 0,
 virtual_sales_count int NULL DEFAULT 0,
 browse_count int NULL DEFAULT 0,
 create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 creator varchar(64) NULL DEFAULT NULL,
 updater varchar(64) NULL DEFAULT NULL,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS product_statistics;

CREATE TABLE product_statistics (
 id BIGSERIAL,
 time date NOT NULL,
 spu_id bigint NOT NULL,
 browse_count int NOT NULL DEFAULT 0,
 browse_user_count int NOT NULL DEFAULT 0,
 favorite_count int NOT NULL DEFAULT 0,
 cart_count int NOT NULL DEFAULT 0,
 order_count int NOT NULL DEFAULT 0,
 order_pay_count int NOT NULL DEFAULT 0,
 order_pay_price int NOT NULL DEFAULT 0,
 after_sale_count int NOT NULL DEFAULT 0,
 after_sale_refund_price int NOT NULL DEFAULT 0,
 browse_convert_percent int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_article;

CREATE TABLE promotion_article (
 id bigint NOT NULL ,
 category_id bigint NOT NULL,
 spu_id bigint NOT NULL DEFAULT 0,
 title varchar(255) NOT NULL,
 author varchar(255) NULL DEFAULT '',
 pic_url varchar(255) NOT NULL,
 introduction varchar(255) NULL DEFAULT '',
 browse_count varchar(255) NULL DEFAULT '',
 sort int NOT NULL DEFAULT 0,
 status SMALLINT NOT NULL DEFAULT 0,
 recommend_hot BOOLEAN NOT NULL DEFAULT FALSE,
 recommend_banner BOOLEAN NOT NULL DEFAULT FALSE,
 content text NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_article_category;

CREATE TABLE promotion_article_category (
 id BIGSERIAL,
 name varchar(255) NOT NULL,
 pic_url varchar(255) NULL DEFAULT '',
 status SMALLINT NOT NULL DEFAULT 1,
 sort int NOT NULL DEFAULT 99999,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_banner;

CREATE TABLE promotion_banner (
 id BIGSERIAL,
 title varchar(64) NOT NULL DEFAULT '',
 pic_url varchar(255) NOT NULL,
 url varchar(255) NOT NULL,
 status SMALLINT NOT NULL DEFAULT -1,
 sort int NULL DEFAULT NULL,
 position SMALLINT NOT NULL,
 memo varchar(255) NULL DEFAULT NULL,
 browse_count int NULL DEFAULT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_bargain_activity;

CREATE TABLE promotion_bargain_activity (
 id BIGSERIAL,
 name varchar(200) NOT NULL DEFAULT '',
 start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 end_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 status int NOT NULL DEFAULT 0,
 spu_id bigint NOT NULL DEFAULT 0,
 sku_id bigint NOT NULL,
 bargain_first_price int NOT NULL DEFAULT 0,
 bargain_min_price int NOT NULL DEFAULT 0,
 stock int NOT NULL DEFAULT 0,
 total_stock int NOT NULL DEFAULT 0,
 help_max_count int NOT NULL DEFAULT 0,
 bargain_count int NOT NULL DEFAULT 0,
 total_limit_count int NOT NULL DEFAULT 0,
 random_min_price int NOT NULL DEFAULT 0,
 random_max_price int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_bargain_help;

CREATE TABLE promotion_bargain_help (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 activity_id bigint NOT NULL,
 record_id bigint NOT NULL DEFAULT 0,
 reduce_price int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_bargain_record;

CREATE TABLE promotion_bargain_record (
 id BIGSERIAL,
 activity_id bigint NOT NULL,
 user_id bigint NOT NULL,
 spu_id bigint NOT NULL DEFAULT 0,
 sku_id bigint NOT NULL,
 bargain_first_price int NOT NULL DEFAULT 0,
 bargain_price int NOT NULL DEFAULT 0,
 status int NOT NULL DEFAULT 0,
 order_id bigint NULL DEFAULT NULL,
 end_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_combination_activity;

CREATE TABLE promotion_combination_activity (
 id BIGSERIAL,
 name varchar(50) NOT NULL DEFAULT '',
 spu_id bigint NOT NULL,
 total_limit_count int NOT NULL,
 single_limit_count int NOT NULL,
 start_time TIMESTAMP NOT NULL,
 end_time TIMESTAMP NOT NULL,
 user_size int NULL DEFAULT NULL,
 virtual_group int NOT NULL,
 status SMALLINT NOT NULL DEFAULT 0,
 limit_duration int NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_combination_product;

CREATE TABLE promotion_combination_product (
 id BIGSERIAL,
 activity_id bigint NULL DEFAULT NULL,
 spu_id bigint NULL DEFAULT NULL,
 sku_id bigint NULL DEFAULT NULL,
 activity_status SMALLINT NOT NULL DEFAULT 0,
 activity_start_time TIMESTAMP NOT NULL,
 activity_end_time TIMESTAMP NOT NULL,
 combination_price int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_combination_record;

CREATE TABLE promotion_combination_record (
 id BIGSERIAL,
 activity_id bigint NULL DEFAULT NULL,
 spu_id bigint NULL DEFAULT NULL,
 pic_url varchar(255) NOT NULL,
 spu_name varchar(64) NOT NULL,
 sku_id bigint NULL DEFAULT NULL,
 count int NULL DEFAULT NULL,
 user_id bigint NULL DEFAULT NULL,
 nickname varchar(64) NULL DEFAULT '',
 avatar varchar(255) NULL DEFAULT '',
 head_id bigint NULL DEFAULT NULL,
 order_id bigint NULL DEFAULT NULL,
 user_size int NOT NULL,
 user_count int NOT NULL,
 virtual_group BOOLEAN NULL DEFAULT NULL,
 status SMALLINT NOT NULL DEFAULT 0,
 combination_price int NOT NULL,
 expire_time TIMESTAMP NOT NULL,
 start_time TIMESTAMP NULL DEFAULT NULL,
 end_time TIMESTAMP NULL DEFAULT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_coupon;

CREATE TABLE promotion_coupon (
 id BIGSERIAL,
 template_id bigint NOT NULL,
 name varchar(50) NOT NULL,
 status SMALLINT NOT NULL,
 user_id bigint NOT NULL,
 take_type SMALLINT NOT NULL,
 use_price int NOT NULL,
 valid_start_time TIMESTAMP NOT NULL,
 valid_end_time TIMESTAMP NOT NULL,
 product_scope SMALLINT NOT NULL,
 product_scope_values varchar(500) NULL DEFAULT NULL,
 discount_type SMALLINT NOT NULL,
 discount_percent SMALLINT NULL DEFAULT NULL,
 discount_price int NULL DEFAULT NULL,
 discount_limit_price int NULL DEFAULT NULL,
 use_order_id bigint NULL DEFAULT NULL,
 use_time TIMESTAMP NULL DEFAULT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_coupon_template;

CREATE TABLE promotion_coupon_template (
 id BIGSERIAL,
 name varchar(50) NOT NULL,
 description varchar(512) NULL DEFAULT NULL,
 status SMALLINT NOT NULL,
 total_count int NOT NULL,
 take_limit_count SMALLINT NOT NULL,
 take_type SMALLINT NOT NULL,
 use_price int NOT NULL,
 product_scope SMALLINT NOT NULL,
 product_scope_values varchar(500) NULL DEFAULT NULL,
 validity_type SMALLINT NOT NULL,
 valid_start_time TIMESTAMP NULL DEFAULT NULL,
 valid_end_time TIMESTAMP NULL DEFAULT NULL,
 fixed_start_term int NULL DEFAULT NULL,
 fixed_end_term int NULL DEFAULT NULL,
 discount_type int NOT NULL,
 discount_percent SMALLINT NULL DEFAULT NULL,
 discount_price int NULL DEFAULT NULL,
 discount_limit_price int NULL DEFAULT NULL,
 take_count int NOT NULL DEFAULT 0,
 use_count int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_discount_activity;

CREATE TABLE promotion_discount_activity (
 id BIGSERIAL,
 name varchar(50) NOT NULL DEFAULT '',
 status SMALLINT NOT NULL DEFAULT -1,
 start_time TIMESTAMP NOT NULL,
 end_time TIMESTAMP NOT NULL,
 remark varchar(255) NULL DEFAULT '',
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_discount_product;

CREATE TABLE promotion_discount_product (
 id BIGSERIAL,
 activity_id bigint NOT NULL,
 spu_id bigint NOT NULL DEFAULT -1,
 sku_id bigint NOT NULL,
 discount_type int NOT NULL,
 discount_percent smallint NULL DEFAULT NULL,
 discount_price int NULL DEFAULT NULL,
 activity_status SMALLINT NOT NULL DEFAULT 0,
 activity_name varchar(50) NOT NULL DEFAULT '',
 activity_start_time TIMESTAMP NOT NULL,
 activity_end_time TIMESTAMP NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_diy_page;

CREATE TABLE promotion_diy_page (
 id BIGSERIAL,
 template_id bigint NULL DEFAULT NULL,
 name varchar(100) NOT NULL,
 remark varchar(255) NULL DEFAULT NULL,
 preview_pic_urls varchar(2000) NULL DEFAULT NULL,
 property text NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_diy_template;

CREATE TABLE promotion_diy_template (
 id BIGSERIAL,
 name varchar(100) NOT NULL,
 used BOOLEAN NOT NULL DEFAULT FALSE,
 used_time TIMESTAMP NULL DEFAULT NULL,
 remark varchar(255) NULL DEFAULT NULL,
 preview_pic_urls varchar(2000) NULL DEFAULT NULL,
 property text NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_kefu_conversation;

CREATE TABLE promotion_kefu_conversation (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 last_message_time TIMESTAMP NOT NULL,
 last_message_content varchar(2048) NOT NULL,
 last_message_content_type int NOT NULL,
 admin_pinned BOOLEAN NOT NULL DEFAULT FALSE,
 user_deleted BOOLEAN NOT NULL DEFAULT FALSE,
 admin_deleted BOOLEAN NOT NULL DEFAULT FALSE,
 admin_unread_message_count int NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_kefu_message;

CREATE TABLE promotion_kefu_message (
 id BIGSERIAL,
 conversation_id bigint NOT NULL,
 sender_id bigint NOT NULL,
 sender_type int NOT NULL,
 receiver_id bigint NULL DEFAULT NULL,
 receiver_type int NULL DEFAULT NULL,
 content_type int NOT NULL,
 content varchar(2048) NOT NULL,
 read_status BOOLEAN NOT NULL DEFAULT FALSE,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_point_activity;

CREATE TABLE promotion_point_activity (
 id BIGSERIAL,
 spu_id bigint NOT NULL,
 status int NOT NULL,
 remark varchar(255) NULL DEFAULT NULL,
 sort int NOT NULL,
 stock int NOT NULL,
 total_stock int NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_point_product;

CREATE TABLE promotion_point_product (
 id BIGSERIAL,
 activity_id bigint NOT NULL,
 spu_id bigint NOT NULL,
 sku_id bigint NOT NULL,
 count int NOT NULL,
 point int NOT NULL,
 price int NOT NULL,
 stock int NOT NULL,
 activity_status int NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_reward_activity;

CREATE TABLE promotion_reward_activity (
 id BIGSERIAL,
 name varchar(50) NOT NULL DEFAULT '',
 status SMALLINT NOT NULL DEFAULT -1,
 start_time TIMESTAMP NOT NULL,
 end_time TIMESTAMP NOT NULL,
 remark varchar(255) NULL DEFAULT '',
 condition_type SMALLINT NOT NULL DEFAULT -1,
 product_scope SMALLINT NOT NULL,
 product_scope_values varchar(1024) NULL DEFAULT NULL,
 rules varchar(2000) NULL DEFAULT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_seckill_activity;

CREATE TABLE promotion_seckill_activity (
 id BIGSERIAL,
 spu_id bigint NOT NULL DEFAULT 0,
 name varchar(255) NOT NULL DEFAULT '',
 status SMALLINT NOT NULL DEFAULT 0,
 remark varchar(1000) NULL DEFAULT '',
 start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 end_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 sort int NOT NULL DEFAULT 0,
 config_ids varchar(255) NOT NULL DEFAULT '0',
 total_limit_count int NULL DEFAULT 0,
 single_limit_count int NULL DEFAULT 0,
 stock int NULL DEFAULT 0,
 total_stock int NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_seckill_config;

CREATE TABLE promotion_seckill_config (
 id BIGSERIAL,
 name varchar(255) NOT NULL,
 start_time varchar(25) NOT NULL,
 end_time varchar(25) NOT NULL,
 slider_pic_urls varchar(1024) NOT NULL,
 status SMALLINT NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS promotion_seckill_product;

CREATE TABLE promotion_seckill_product (
 id BIGSERIAL,
 activity_id bigint NOT NULL DEFAULT 0,
 config_ids varchar(100) NOT NULL DEFAULT '0',
 spu_id bigint NOT NULL DEFAULT 0,
 sku_id bigint NOT NULL DEFAULT 0,
 seckill_price int NOT NULL DEFAULT 0,
 stock int NOT NULL DEFAULT 0,
 activity_status SMALLINT NOT NULL DEFAULT 0,
 activity_start_time TIMESTAMP NOT NULL,
 activity_end_time TIMESTAMP NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_after_sale;

CREATE TABLE trade_after_sale (
 id BIGSERIAL,
 no varchar(32) NOT NULL,
 type SMALLINT NULL DEFAULT NULL,
 status int NOT NULL DEFAULT 0,
 way SMALLINT NOT NULL,
 user_id bigint NOT NULL,
 apply_reason varchar(255) NOT NULL,
 apply_description varchar(255) NULL DEFAULT NULL,
 apply_pic_urls varchar(255) NULL DEFAULT NULL,
 order_id bigint NOT NULL,
 order_no varchar(32) NOT NULL,
 order_item_Id bigint NOT NULL,
 spu_id bigint NOT NULL,
 spu_name varchar(255) NOT NULL,
 sku_id bigint NOT NULL,
 properties JSONB NULL,
 pic_url varchar(200) NULL DEFAULT NULL,
 count int NOT NULL,
 audit_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 audit_user_id bigint NULL DEFAULT NULL,
 audit_reason varchar(255) NULL DEFAULT NULL,
 refund_price int NOT NULL DEFAULT 0,
 pay_refund_id bigint NULL DEFAULT NULL,
 refund_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
 logistics_id bigint NULL DEFAULT NULL,
 logistics_no varchar(64) NULL DEFAULT NULL,
 delivery_time TIMESTAMP NULL DEFAULT NULL,
 receive_time TIMESTAMP NULL DEFAULT NULL,
 receive_reason varchar(255) NULL DEFAULT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_after_sale_log;

CREATE TABLE trade_after_sale_log (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 user_type SMALLINT NOT NULL,
 after_sale_id bigint NOT NULL,
 before_status SMALLINT NULL DEFAULT NULL,
 after_status SMALLINT NOT NULL,
 operate_type SMALLINT NOT NULL,
 content varchar(512) NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_brokerage_record;

CREATE TABLE trade_brokerage_record (
 id SERIAL,
 user_id bigint NOT NULL,
 biz_id varchar(64) NOT NULL DEFAULT '',
 biz_type SMALLINT NOT NULL DEFAULT 0,
 title varchar(64) NOT NULL DEFAULT '',
 price int NOT NULL DEFAULT 0,
 total_price int NOT NULL DEFAULT 0,
 description varchar(500) NOT NULL DEFAULT '',
 status SMALLINT NOT NULL DEFAULT 0,
 frozen_days int NOT NULL DEFAULT 0,
 unfreeze_time TIMESTAMP NULL DEFAULT NULL,
 source_user_level int NOT NULL DEFAULT 0,
 source_user_id bigint NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_brokerage_user;

CREATE TABLE trade_brokerage_user (
 id BIGSERIAL,
 bind_user_id bigint NULL DEFAULT NULL,
 bind_user_time TIMESTAMP NULL DEFAULT NULL,
 brokerage_enabled BOOLEAN NOT NULL DEFAULT TRUE,
 brokerage_time TIMESTAMP NULL DEFAULT NULL,
 brokerage_price int NOT NULL DEFAULT 0,
 frozen_price int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_brokerage_withdraw;

CREATE TABLE trade_brokerage_withdraw (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 price int NOT NULL DEFAULT 0,
 fee_price int NOT NULL DEFAULT 0,
 total_price int NOT NULL DEFAULT 0,
 type SMALLINT NOT NULL DEFAULT 0,
 user_name varchar(64) NULL DEFAULT NULL,
 user_account varchar(64) NULL DEFAULT NULL,
 bank_name varchar(100) NULL DEFAULT NULL,
 bank_address varchar(200) NULL DEFAULT NULL,
 qr_code_url varchar(512) NULL DEFAULT NULL,
 status SMALLINT NOT NULL DEFAULT 0,
 audit_reason varchar(128) NULL DEFAULT NULL,
 audit_time TIMESTAMP NULL DEFAULT NULL,
 remark varchar(500) NULL DEFAULT NULL,
 pay_transfer_id bigint NULL DEFAULT NULL,
 transfer_channel_code varchar(16) NULL DEFAULT NULL,
 transfer_time TIMESTAMP NULL DEFAULT NULL,
 transfer_error_msg varchar(4096) NULL DEFAULT '',
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_cart;

CREATE TABLE trade_cart (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 spu_id bigint NOT NULL,
 sku_id bigint NOT NULL,
 count int NOT NULL,
 selected BOOLEAN NOT NULL DEFAULT TRUE,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_config;

CREATE TABLE trade_config (
 id BIGSERIAL,
 after_sale_refund_reasons varchar(512) NOT NULL DEFAULT '',
 after_sale_return_reasons varchar(512) NOT NULL DEFAULT '',
 delivery_express_free_enabled BOOLEAN NOT NULL DEFAULT FALSE,
 delivery_express_free_price int NOT NULL DEFAULT 0,
 delivery_pick_up_enabled BOOLEAN NOT NULL DEFAULT FALSE,
 brokerage_enabled BOOLEAN NOT NULL DEFAULT FALSE,
 brokerage_enabled_condition SMALLINT NOT NULL DEFAULT 1,
 brokerage_bind_mode SMALLINT NOT NULL DEFAULT 1,
 brokerage_poster_urls varchar(2000) NULL DEFAULT '',
 brokerage_first_percent int NOT NULL DEFAULT 0,
 brokerage_second_percent int NOT NULL DEFAULT 0,
 brokerage_withdraw_min_price int NOT NULL DEFAULT 0,
 brokerage_withdraw_fee_percent int NOT NULL DEFAULT 0,
 brokerage_frozen_days int NOT NULL DEFAULT 7,
 brokerage_withdraw_types varchar(32) NOT NULL DEFAULT '1,2,3,4',
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_delivery_express;

CREATE TABLE trade_delivery_express (
 id BIGSERIAL,
 code varchar(64) NOT NULL,
 name varchar(64) NOT NULL,
 logo varchar(256) NULL DEFAULT NULL,
 sort int NOT NULL DEFAULT 0,
 status SMALLINT NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_delivery_express_template;

CREATE TABLE trade_delivery_express_template (
 id BIGSERIAL,
 name varchar(64) NOT NULL,
 charge_mode SMALLINT NOT NULL,
 sort int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_delivery_express_template_charge;

CREATE TABLE trade_delivery_express_template_charge (
 id BIGSERIAL,
 template_id bigint NOT NULL,
 area_ids text NOT NULL,
 charge_mode SMALLINT NOT NULL,
 start_count DOUBLE PRECISION NOT NULL,
 start_price int NOT NULL,
 extra_count DOUBLE PRECISION NOT NULL,
 extra_price int NOT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_delivery_express_template_free;

CREATE TABLE trade_delivery_express_template_free (
 id BIGSERIAL,
 template_id bigint NOT NULL,
 area_ids text NOT NULL,
 free_price int NOT NULL,
 free_count int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_delivery_pick_up_store;

CREATE TABLE trade_delivery_pick_up_store (
 id BIGSERIAL,
 name varchar(64) NOT NULL,
 introduction varchar(256) NULL DEFAULT NULL,
 phone varchar(16) NOT NULL,
 area_id int NOT NULL,
 detail_address varchar(256) NOT NULL,
 logo varchar(256) NOT NULL,
 opening_time time NOT NULL,
 closing_time time NOT NULL,
 latitude DOUBLE PRECISION NOT NULL,
 longitude DOUBLE PRECISION NOT NULL,
 verify_user_ids varchar(256) NULL DEFAULT NULL,
 status SMALLINT NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_order;

CREATE TABLE trade_order (
 id BIGSERIAL,
 no varchar(32) NOT NULL,
 type int NOT NULL DEFAULT 0,
 terminal int NOT NULL,
 user_id bigint NOT NULL,
 user_ip varchar(30) NOT NULL DEFAULT '',
 user_remark varchar(200) NULL DEFAULT NULL,
 status int NOT NULL DEFAULT 0,
 product_count int NOT NULL,
 cancel_type int NULL DEFAULT NULL,
 remark varchar(200) NULL DEFAULT NULL,
 comment_status BOOLEAN NOT NULL DEFAULT FALSE,
 brokerage_user_id bigint NULL DEFAULT NULL,
 pay_order_id bigint NULL DEFAULT NULL,
 pay_status BOOLEAN NOT NULL DEFAULT FALSE,
 pay_time TIMESTAMP NULL DEFAULT NULL,
 pay_channel_code varchar(16) NULL DEFAULT NULL,
 finish_time TIMESTAMP NULL DEFAULT NULL,
 cancel_time TIMESTAMP NULL DEFAULT NULL,
 total_price int NOT NULL DEFAULT 0,
 discount_price int NOT NULL DEFAULT 0,
 delivery_price int NOT NULL DEFAULT 0,
 adjust_price int NOT NULL DEFAULT 0,
 pay_price int NOT NULL DEFAULT 0,
 delivery_type SMALLINT NOT NULL,
 logistics_id bigint NULL DEFAULT NULL,
 logistics_no varchar(64) NULL DEFAULT NULL,
 delivery_time TIMESTAMP NULL DEFAULT NULL,
 receive_time TIMESTAMP NULL DEFAULT NULL,
 receiver_name varchar(20) NOT NULL,
 receiver_mobile varchar(20) NOT NULL,
 receiver_area_id int NULL DEFAULT NULL,
 receiver_detail_address varchar(255) NULL DEFAULT NULL,
 pick_up_store_id bigint NULL DEFAULT NULL,
 pick_up_verify_code varchar(64) NULL DEFAULT NULL,
 refund_status SMALLINT NOT NULL DEFAULT 0,
 refund_price int NOT NULL DEFAULT 0,
 coupon_id bigint NULL DEFAULT NULL,
 coupon_price int NOT NULL DEFAULT 0,
 use_point int NOT NULL DEFAULT 0,
 point_price int NOT NULL DEFAULT 0,
 give_point int NOT NULL DEFAULT 0,
 refund_point int NOT NULL DEFAULT 0,
 vip_price int NOT NULL DEFAULT 0,
 give_coupon_template_counts varchar(255) NULL DEFAULT NULL,
 give_coupon_ids varchar(255) NULL DEFAULT NULL,
 seckill_activity_id bigint NULL DEFAULT NULL,
 bargain_activity_id bigint NULL DEFAULT NULL,
 bargain_record_id bigint NULL DEFAULT NULL,
 combination_activity_id bigint NULL DEFAULT NULL,
 combination_head_id bigint NULL DEFAULT NULL,
 combination_record_id bigint NULL DEFAULT NULL,
 point_activity_id bigint NULL DEFAULT NULL,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_order_item;

CREATE TABLE trade_order_item (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 order_id bigint NOT NULL,
 cart_id bigint NULL DEFAULT NULL,
 spu_id bigint NOT NULL,
 spu_name varchar(255) NOT NULL,
 sku_id bigint NOT NULL,
 properties JSONB NULL,
 pic_url varchar(200) NULL DEFAULT NULL,
 count int NOT NULL,
 comment_status BOOLEAN NOT NULL DEFAULT FALSE,
 price int NOT NULL DEFAULT 0,
 discount_price int NOT NULL DEFAULT 0,
 delivery_price int NOT NULL DEFAULT 0,
 adjust_price int NOT NULL DEFAULT 0,
 pay_price int NOT NULL DEFAULT 0,
 coupon_price int NOT NULL DEFAULT 0,
 point_price int NOT NULL DEFAULT 0,
 use_point int NOT NULL DEFAULT 0,
 give_point int NOT NULL DEFAULT 0,
 vip_price int NOT NULL DEFAULT 0,
 after_sale_id bigint NULL DEFAULT NULL,
 after_sale_status int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_order_log;

CREATE TABLE trade_order_log (
 id BIGSERIAL,
 user_id bigint NOT NULL,
 user_type SMALLINT NOT NULL DEFAULT 0,
 order_id bigint NOT NULL,
 before_status SMALLINT NULL DEFAULT NULL,
 after_status SMALLINT NULL DEFAULT NULL,
 operate_type int NOT NULL DEFAULT 0,
 content varchar(2000) NOT NULL DEFAULT '',
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));

DROP TABLE IF EXISTS trade_statistics;

CREATE TABLE trade_statistics (
 id BIGSERIAL,
 time TIMESTAMP NOT NULL,
 order_create_count int NOT NULL DEFAULT 0,
 order_pay_count int NOT NULL DEFAULT 0,
 order_pay_price int NOT NULL DEFAULT 0,
 after_sale_count int NOT NULL DEFAULT 0,
 after_sale_refund_price int NOT NULL DEFAULT 0,
 brokerage_settlement_price int NOT NULL DEFAULT 0,
 wallet_pay_price int NOT NULL DEFAULT 0,
 recharge_pay_count int NOT NULL DEFAULT 0,
 recharge_pay_price int NOT NULL DEFAULT 0,
 recharge_refund_count int NOT NULL DEFAULT 0,
 recharge_refund_price int NOT NULL DEFAULT 0,
 creator varchar(64) NULL DEFAULT '',
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updater varchar(64) NULL DEFAULT '',
 update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 deleted BOOLEAN NOT NULL DEFAULT FALSE,
 tenant_id bigint NOT NULL DEFAULT 0,
 PRIMARY KEY (id));