-- =============================================
-- 训练模块 - PostgreSQL 数据库脚本
-- 包含: 训练模板、训练会话、训练日志
-- =============================================

-- ----------------------------
-- 1. 训练模板表
-- ----------------------------
DROP TABLE IF EXISTS training_template;
CREATE TABLE training_template (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    domain VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    brief_goal TEXT,
    recommended_frequency VARCHAR(100),
    base_scenario TEXT,
    level1_rules TEXT,
    level2_rules TEXT,
    level3_rules TEXT,
    extra_meta TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    sort INT DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

CREATE INDEX idx_training_template_domain ON training_template(domain);
CREATE INDEX idx_training_template_code ON training_template(code);
CREATE UNIQUE INDEX uk_training_template_code ON training_template(code) WHERE deleted = FALSE;

COMMENT ON TABLE training_template IS '训练模板表';
COMMENT ON COLUMN training_template.id IS '模板ID';
COMMENT ON COLUMN training_template.code IS '模板编码，如 S01, L03';
COMMENT ON COLUMN training_template.domain IS '训练域: social/language/emotion/cognition/sensory/daily_living';
COMMENT ON COLUMN training_template.name IS '训练名称';
COMMENT ON COLUMN training_template.brief_goal IS '简要目标（一句话）';
COMMENT ON COLUMN training_template.recommended_frequency IS '推荐训练频率';
COMMENT ON COLUMN training_template.base_scenario IS '基础场景描述';
COMMENT ON COLUMN training_template.level1_rules IS 'Level 1 规则（JSON）';
COMMENT ON COLUMN training_template.level2_rules IS 'Level 2 规则（JSON）';
COMMENT ON COLUMN training_template.level3_rules IS 'Level 3 规则（JSON）';
COMMENT ON COLUMN training_template.extra_meta IS '额外元数据（JSON）';
COMMENT ON COLUMN training_template.is_active IS '是否启用';
COMMENT ON COLUMN training_template.sort IS '排序';

-- ----------------------------
-- 2. 训练会话表
-- ----------------------------
DROP TABLE IF EXISTS training_session;
CREATE TABLE training_session (
    id BIGSERIAL PRIMARY KEY,
    child_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    scheduled_at TIMESTAMP,
    difficulty_level INT DEFAULT 1,
    ai_input_json TEXT,
    ai_output_json TEXT,
    status INT DEFAULT 0,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    actual_duration_minutes INT,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

CREATE INDEX idx_training_session_child_id ON training_session(child_id);
CREATE INDEX idx_training_session_user_id ON training_session(user_id);
CREATE INDEX idx_training_session_template_id ON training_session(template_id);
CREATE INDEX idx_training_session_scheduled_at ON training_session(scheduled_at);
CREATE INDEX idx_training_session_status ON training_session(status);

COMMENT ON TABLE training_session IS '训练会话表';
COMMENT ON COLUMN training_session.id IS '会话ID';
COMMENT ON COLUMN training_session.child_id IS '孩子ID';
COMMENT ON COLUMN training_session.user_id IS '用户ID（家长）';
COMMENT ON COLUMN training_session.template_id IS '模板ID';
COMMENT ON COLUMN training_session.scheduled_at IS '计划训练时间';
COMMENT ON COLUMN training_session.difficulty_level IS '难度等级 1/2/3';
COMMENT ON COLUMN training_session.ai_input_json IS '传给大模型的输入JSON';
COMMENT ON COLUMN training_session.ai_output_json IS '大模型生成的训练卡片JSON';
COMMENT ON COLUMN training_session.status IS '状态: 0-已计划 1-进行中 2-已完成 3-已跳过';
COMMENT ON COLUMN training_session.started_at IS '开始时间';
COMMENT ON COLUMN training_session.completed_at IS '完成时间';
COMMENT ON COLUMN training_session.actual_duration_minutes IS '实际训练时长（分钟）';

-- ----------------------------
-- 3. 训练日志表
-- ----------------------------
DROP TABLE IF EXISTS training_log;
CREATE TABLE training_log (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    child_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    template_id BIGINT,
    domain VARCHAR(50),
    done_at TIMESTAMP NOT NULL,
    completed BOOLEAN DEFAULT TRUE,
    child_mood INT,
    success_count INT,
    difficulties TEXT,
    parent_comment TEXT,
    structured_data TEXT,
    shared_to_community BOOLEAN DEFAULT FALSE,
    ai_feedback TEXT,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

CREATE INDEX idx_training_log_session_id ON training_log(session_id);
CREATE INDEX idx_training_log_child_id ON training_log(child_id);
CREATE INDEX idx_training_log_user_id ON training_log(user_id);
CREATE INDEX idx_training_log_template_id ON training_log(template_id);
CREATE INDEX idx_training_log_done_at ON training_log(done_at);
CREATE INDEX idx_training_log_domain ON training_log(domain);

COMMENT ON TABLE training_log IS '训练日志表';
COMMENT ON COLUMN training_log.id IS '日志ID';
COMMENT ON COLUMN training_log.session_id IS '会话ID';
COMMENT ON COLUMN training_log.child_id IS '孩子ID';
COMMENT ON COLUMN training_log.user_id IS '用户ID（家长）';
COMMENT ON COLUMN training_log.template_id IS '模板ID（冗余）';
COMMENT ON COLUMN training_log.domain IS '训练域（冗余）';
COMMENT ON COLUMN training_log.done_at IS '打卡时间';
COMMENT ON COLUMN training_log.completed IS '是否完成';
COMMENT ON COLUMN training_log.child_mood IS '孩子情绪: 1-还不错 2-一般 3-情绪不好';
COMMENT ON COLUMN training_log.success_count IS '成功次数';
COMMENT ON COLUMN training_log.difficulties IS '遇到的困难（JSON数组）';
COMMENT ON COLUMN training_log.parent_comment IS '家长备注';
COMMENT ON COLUMN training_log.structured_data IS '结构化数据（JSON）';
COMMENT ON COLUMN training_log.shared_to_community IS '是否分享到社区';
COMMENT ON COLUMN training_log.ai_feedback IS 'AI反馈内容（JSON）';

-- =============================================
-- 4. 初始化 60 个训练模板数据
-- =============================================

-- 社交与互动（S01-S10）
INSERT INTO training_template (code, domain, name, brief_goal, recommended_frequency, base_scenario, level1_rules, level2_rules, level3_rules, sort) VALUES
('S01', 'social', '回应名字', '当家长叫名字时，孩子能有转头、停顿或看向家长的反应。', '每天 5-10 次，穿插在日常生活中', '在家中日常活动场景中，通过多次、有强化的叫名练习，建立"名字=注意到大人"的联系。', 
'{"distance": "1米内", "prompt": "名字+物品+肢体夸张吸引", "success_criteria": "有任何反应即强化"}',
'{"distance": "2-3米", "prompt": "减少物品诱导，自然互动", "success_criteria": "2-3次尝试内有清晰反应"}',
'{"distance": "不同场景", "prompt": "未事先提醒的自然情境", "success_criteria": "不同人、不同场景都能回应"}', 1),

('S02', 'social', '共同注意：跟随指物', '当大人指向某个物品时，孩子能把视线转向该物品。', '每天 3-5 次', '在有吸引力物品的环境中，引导孩子跟随大人的手指方向看向目标。',
'{"distance": "近距离", "target": "高吸引力物品", "success_criteria": "有看向目标的意图"}',
'{"distance": "稍远", "target": "普通物品", "success_criteria": "能准确看向指定物品"}',
'{"distance": "多场景", "target": "任意物品", "success_criteria": "快速准确跟随"}', 2),

('S03', 'social', '展示物品给大人看', '孩子愿意把手里的东西拿给大人看，建立分享的习惯。', '每天 3 次以上', '当孩子拿到感兴趣的物品时，引导他展示给大人看。',
'{"prompt": "大人主动靠近表示兴趣", "success_criteria": "允许大人看物品"}',
'{"prompt": "语言引导-给我看看", "success_criteria": "主动举起展示"}',
'{"prompt": "无提示", "success_criteria": "自发展示新发现"}', 3),

('S04', 'social', '轮流：你一下我一下', '孩子能和大人进行简单的轮流游戏（比如轮流丢球）。', '每天至少 1 个短局', '用孩子喜欢的玩具，建立轮流互动的模式。',
'{"rounds": "2-3轮", "prompt": "手把手辅助", "success_criteria": "理解轮到自己"}',
'{"rounds": "5轮以上", "prompt": "语言提示", "success_criteria": "等待轮到自己"}',
'{"rounds": "多轮", "prompt": "规则理解", "success_criteria": "主动等待和给予"}', 4),

('S05', 'social', '模仿：拍手', '孩子能模仿大人的拍手动作。', '每天 5 次左右', '通过示范和强化，建立动作模仿的意识。',
'{"prompt": "手把手辅助", "timing": "立即", "success_criteria": "有模仿意图"}',
'{"prompt": "示范后等待", "timing": "3秒内", "success_criteria": "独立模仿"}',
'{"prompt": "语言指令", "timing": "立即", "success_criteria": "听到指令就做"}', 5),

('S06', 'social', '模仿：敲桌子/地面', '孩子能模仿大人的敲击动作，提升模仿和共同活动。', '每天 3-5 次', '用有节奏的敲击游戏，引导孩子跟随模仿。',
'{"prompt": "手把手辅助", "rhythm": "简单", "success_criteria": "有敲击动作"}',
'{"prompt": "示范", "rhythm": "2-3下", "success_criteria": "跟随敲击"}',
'{"prompt": "变化节奏", "rhythm": "复杂", "success_criteria": "模仿节奏变化"}', 6),

('S07', 'social', '短暂对视 1 秒', '孩子能短暂地看向大人眼睛或脸部。', '每天在自然互动中多次尝试', '在孩子需要帮助或期待奖励时，引导建立短暂对视。',
'{"duration": "瞬间", "trigger": "有强吸引物", "success_criteria": "有看向脸部的动作"}',
'{"duration": "1秒", "trigger": "语言引导", "success_criteria": "维持短暂对视"}',
'{"duration": "2秒以上", "trigger": "自然情境", "success_criteria": "主动寻求对视"}', 7),

('S08', 'social', '主动拍大人引起注意', '孩子想要什么时，可以主动拍大人而不是只是哭或拉扯。', '每天观察和捕捉自然机会', '当孩子有需求时，引导用拍打方式引起注意。',
'{"prompt": "手把手带着拍", "timing": "需求强烈时", "success_criteria": "被动拍"}',
'{"prompt": "语言提示", "timing": "有需求时", "success_criteria": "提示后拍"}',
'{"prompt": "无提示", "timing": "日常", "success_criteria": "主动拍"}', 8),

('S09', 'social', '拉大人去需要的地方', '孩子有需要时，能主动拉大人的手往目标方向走。', '每天 2-3 次', '创造需要帮助的场景，引导孩子用拉手表达需求。',
'{"prompt": "大人伸手等待", "distance": "近", "success_criteria": "接受被拉"}',
'{"prompt": "语言引导", "distance": "中", "success_criteria": "主动拉手"}',
'{"prompt": "无提示", "distance": "远", "success_criteria": "明确方向引导"}', 9),

('S10', 'social', '挥手打招呼/再见', '在见面或离开时，孩子能模仿大人挥手。', '每天在真实场景中练习', '利用日常出入场景，建立挥手的社交习惯。',
'{"prompt": "手把手带挥", "timing": "分离时", "success_criteria": "配合挥手"}',
'{"prompt": "示范", "timing": "见面或离开", "success_criteria": "模仿挥手"}',
'{"prompt": "语言", "timing": "各种场合", "success_criteria": "主动挥手"}', 10);

-- 语言与沟通（L01-L10）
INSERT INTO training_template (code, domain, name, brief_goal, recommended_frequency, base_scenario, level1_rules, level2_rules, level3_rules, sort) VALUES
('L01', 'language', '听懂"一起"', '孩子能理解"我们一起做某件事"的口令并参与行动。', '每天 3-5 次', '在游戏或活动开始时，用"一起"引导共同参与。',
'{"prompt": "手势+语言", "activity": "喜欢的", "success_criteria": "跟随行动"}',
'{"prompt": "语言为主", "activity": "日常", "success_criteria": "听懂后参与"}',
'{"prompt": "只说一起", "activity": "新活动", "success_criteria": "理解并执行"}', 11),

('L02', 'language', '用伸手表达想要', '孩子想要东西时，用伸手来表达，而不是只哭闹。', '每天多次', '在孩子想要物品时，等待并引导伸手表达。',
'{"prompt": "带着手伸", "wait": "短", "success_criteria": "被动伸手"}',
'{"prompt": "等待+示范", "wait": "中", "success_criteria": "主动伸手"}',
'{"prompt": "等待", "wait": "长", "success_criteria": "伸手+看人"}', 12),

('L03', 'language', '用指物表达想要', '孩子能用手指指向目标物品，表达自己的需求。', '每天 3-5 次', '将物品放在孩子触及不到的地方，引导指向表达。',
'{"prompt": "手把手带指", "target": "高需求", "success_criteria": "手指向目标"}',
'{"prompt": "等待+提示", "target": "日常", "success_criteria": "主动指向"}',
'{"prompt": "等待", "target": "多选项", "success_criteria": "准确指向"}', 13),

('L04', 'language', 'PECS：递图片换物品', '孩子通过递交图片给大人来获得想要的物品。', '每天 3-5 次', '使用图片交换系统，建立沟通桥梁。',
'{"prompt": "手把手递图", "picture": "实物照片", "success_criteria": "完成交换"}',
'{"prompt": "指向图片", "picture": "简单图", "success_criteria": "主动拿递"}',
'{"prompt": "选择正确图", "picture": "多图选择", "success_criteria": "准确选择"}', 14),

('L05', 'language', '理解指令"给我"', '孩子能理解"给我"的指令并把物品递给大人。', '每天 5 次左右', '在自然互动中练习物品传递。',
'{"prompt": "伸手+语言", "object": "手中物", "success_criteria": "放到手上"}',
'{"prompt": "语言", "object": "指定物", "success_criteria": "找到并给"}',
'{"prompt": "语言", "object": "远处物", "success_criteria": "去拿并给"}', 15),

('L06', 'language', '理解"不可以"', '孩子能逐渐理解"不可以"的含义，学会在被制止时停下来。', '根据实际情况出现时使用', '在危险或不当行为时，用清晰的语言和动作制止。',
'{"prompt": "语言+肢体阻止", "tone": "坚定温和", "success_criteria": "停下动作"}',
'{"prompt": "语言+眼神", "tone": "坚定", "success_criteria": "听到即停"}',
'{"prompt": "语言", "tone": "日常", "success_criteria": "理解并遵从"}', 16),

('L07', 'language', '模仿拟声词', '孩子能模仿简单的动物叫声或拟声词（如哞、咩）。', '每天 3-5 次游戏式练习', '通过动物图片或玩具，引导模仿发声。',
'{"prompt": "夸张示范", "sound": "简单音", "success_criteria": "有发声意图"}',
'{"prompt": "示范", "sound": "常见动物", "success_criteria": "近似模仿"}',
'{"prompt": "看图说", "sound": "多种类", "success_criteria": "准确模仿"}', 17),

('L08', 'language', '声音轮流游戏', '孩子能和大人轮流发声，形成初步"你一句我一句"的互动。', '每天 1-2 轮', '用简单的声音游戏建立语言轮流意识。',
'{"prompt": "大人多发", "wait": "短", "success_criteria": "有回应发声"}',
'{"prompt": "等待回应", "wait": "中", "success_criteria": "轮流发声"}',
'{"prompt": "多轮", "wait": "自然", "success_criteria": "持续轮流"}', 18),

('L09', 'language', '说"要"来请求', '孩子能用"要"这个词来表达需求（或相应发音/替代词）。', '每天多次', '在孩子有需求时，等待并引导说出"要"。',
'{"prompt": "示范+等待", "accept": "任何发声", "success_criteria": "有声音表达"}',
'{"prompt": "首音提示", "accept": "近似音", "success_criteria": "说出类似音"}',
'{"prompt": "等待", "accept": "清晰", "success_criteria": "清晰说要"}', 19),

('L10', 'language', '双词组合：要+物品', '孩子能用两词短语表达需求，例如"要 饼干"。', '每天 3-5 次', '在孩子说出单词后，引导扩展为两词组合。',
'{"prompt": "示范完整", "accept": "单词", "success_criteria": "尝试组合"}',
'{"prompt": "首词提示", "accept": "两词", "success_criteria": "两词组合"}',
'{"prompt": "等待", "accept": "流畅", "success_criteria": "自发两词"}', 20);

-- 情绪与行为（E01-E10）
INSERT INTO training_template (code, domain, name, brief_goal, recommended_frequency, base_scenario, level1_rules, level2_rules, level3_rules, sort) VALUES
('E01', 'emotion', '等待 5 秒', '孩子在得到想要的物品前，能短暂等待约 5 秒。', '每天 3-5 次', '在给予物品前，引导短暂等待。',
'{"duration": "2-3秒", "distraction": "有", "success_criteria": "不哭闹"}',
'{"duration": "5秒", "distraction": "少", "success_criteria": "安静等待"}',
'{"duration": "10秒", "distraction": "无", "success_criteria": "耐心等待"}', 21),

('E02', 'emotion', '情绪识别：高兴', '孩子能在图片或真人表情中分辨"高兴"的情绪。', '每周多次', '用表情图片或镜子，帮助识别高兴的表情。',
'{"material": "夸张图", "task": "指认", "success_criteria": "能指出"}',
'{"material": "真人照", "task": "选择", "success_criteria": "正确选择"}',
'{"material": "视频或真人", "task": "判断", "success_criteria": "说出高兴"}', 22),

('E03', 'emotion', '情绪识别：生气', '孩子能在图片或场景中分辨"生气"的表情或情绪。', '每周多次', '用表情图片帮助识别生气的情绪。',
'{"material": "夸张图", "task": "指认", "success_criteria": "能指出"}',
'{"material": "真人照", "task": "选择", "success_criteria": "正确选择"}',
'{"material": "情境", "task": "判断", "success_criteria": "理解情境"}', 23),

('E04', 'emotion', '用拍大人替代尖叫', '孩子在有需求时，学会用拍大人来表达，而不是用尖叫。', '每天捕捉机会强化', '在孩子尖叫时，立即引导替代行为。',
'{"intervention": "立即引导", "prompt": "手把手", "success_criteria": "被动拍"}',
'{"intervention": "预防引导", "prompt": "提示", "success_criteria": "提示后拍"}',
'{"intervention": "等待", "prompt": "无", "success_criteria": "主动拍"}', 24),

('E05', 'emotion', '深呼吸 3 次', '孩子在紧张或不舒服时，能在大人带领下做 3 次深呼吸。', '每天 1 次练习，情绪激动时使用', '平时练习深呼吸，情绪激动时使用。',
'{"prompt": "示范+带做", "count": "1次", "success_criteria": "跟随做"}',
'{"prompt": "语言", "count": "3次", "success_criteria": "独立做"}',
'{"prompt": "提示", "count": "自主", "success_criteria": "主动使用"}', 25),

('E06', 'emotion', '先做 A 再做 B', '孩子能理解"先……再……"的简单任务顺序。', '每天 1-2 次', '用视觉提示帮助理解任务顺序。',
'{"task": "简单", "visual": "图卡", "success_criteria": "按顺序做"}',
'{"task": "日常", "visual": "语言", "success_criteria": "理解顺序"}',
'{"task": "变化", "visual": "无", "success_criteria": "灵活执行"}', 26),

('E07', 'emotion', '用摇头表达"不"', '孩子能用摇头来表达自己不想要某物。', '每天观察机会练习', '在提供不喜欢的东西时，引导用摇头表达。',
'{"prompt": "示范摇头", "situation": "明显不要", "success_criteria": "模仿摇头"}',
'{"prompt": "问是否要", "situation": "选择时", "success_criteria": "主动摇头"}',
'{"prompt": "无", "situation": "日常", "success_criteria": "自发摇头"}', 27),

('E08', 'emotion', '转移注意到新活动', '当孩子过度沉迷某一活动时，大人能带领孩子转移到新的简单活动。', '根据情况使用', '使用预告和吸引策略帮助转移。',
'{"warning": "多次", "new_activity": "高吸引", "success_criteria": "接受转移"}',
'{"warning": "1次", "new_activity": "中等", "success_criteria": "配合转移"}',
'{"warning": "简短", "new_activity": "任意", "success_criteria": "平稳转移"}', 28),

('E09', 'emotion', '把大任务拆成小任务', '面对困难任务时，将其拆成多个小步骤。', '遇到大任务时使用', '用视觉清单帮助理解任务分解。',
'{"steps": "2步", "visual": "图卡", "success_criteria": "完成分步"}',
'{"steps": "3-4步", "visual": "语言", "success_criteria": "按步完成"}',
'{"steps": "多步", "visual": "自主", "success_criteria": "自己分解"}', 29),

('E10', 'emotion', '安静坐 10 秒', '孩子能在桌边或椅子上安静坐约 10 秒。', '每天 1-2 次', '在结构化活动中练习安坐。',
'{"duration": "5秒", "distraction": "有玩具", "success_criteria": "坐住"}',
'{"duration": "10秒", "distraction": "少", "success_criteria": "安静坐"}',
'{"duration": "30秒", "distraction": "无", "success_criteria": "专注坐"}', 30);

-- 认知与规则（C01-C10）
INSERT INTO training_template (code, domain, name, brief_goal, recommended_frequency, base_scenario, level1_rules, level2_rules, level3_rules, sort) VALUES
('C01', 'cognition', '颜色匹配', '孩子能将相同颜色的卡片或物品配对在一起。', '每周 3-5 次', '使用颜色鲜明的材料进行匹配练习。',
'{"colors": "2种", "prompt": "手把手", "success_criteria": "完成配对"}',
'{"colors": "3-4种", "prompt": "语言", "success_criteria": "独立配对"}',
'{"colors": "多种", "prompt": "无", "success_criteria": "快速准确"}', 31),

('C02', 'cognition', '分类：吃的 vs 玩的', '孩子能区分哪些是可以吃的东西，哪些是玩具。', '每周 3 次', '用实物或图片进行分类练习。',
'{"items": "明显差异", "prompt": "示范", "success_criteria": "放对位置"}',
'{"items": "日常物品", "prompt": "语言", "success_criteria": "正确分类"}',
'{"items": "边界物品", "prompt": "无", "success_criteria": "解释分类"}', 32),

('C03', 'cognition', '大小排序：大中小', '孩子能按照大小顺序摆放物品。', '每周 3-5 次', '用大小明显的物品练习排序。',
'{"items": "2个", "prompt": "示范", "success_criteria": "分大小"}',
'{"items": "3个", "prompt": "语言", "success_criteria": "排序"}',
'{"items": "多个", "prompt": "无", "success_criteria": "准确排序"}', 33),

('C04', 'cognition', '简单因果：按按钮有反应', '孩子通过按按钮等行为，理解因果关系。', '每周多次短游戏', '使用因果玩具建立因果概念。',
'{"toy": "反应明显", "prompt": "带做", "success_criteria": "发现关系"}',
'{"toy": "普通", "prompt": "示范", "success_criteria": "主动按"}',
'{"toy": "延迟反应", "prompt": "无", "success_criteria": "预期结果"}', 34),

('C05', 'cognition', '请求帮助而不是乱爬', '当拿不到东西时，孩子能请求帮助。', '抓住机会进行', '创造需要帮助的场景，引导求助行为。',
'{"prompt": "立即帮助+示范", "situation": "简单", "success_criteria": "接受帮助"}',
'{"prompt": "等待+提示", "situation": "日常", "success_criteria": "求助行为"}',
'{"prompt": "等待", "situation": "各种", "success_criteria": "主动求助"}', 35),

('C06', 'cognition', '听从 1 步指令', '孩子能理解并完成简单的一步指令。', '每天多次自然练习', '在日常中给予简单指令并强化完成。',
'{"instruction": "常用", "prompt": "手势+语言", "success_criteria": "完成动作"}',
'{"instruction": "多样", "prompt": "语言", "success_criteria": "独立完成"}',
'{"instruction": "新指令", "prompt": "无", "success_criteria": "理解新指令"}', 36),

('C07', 'cognition', '听从 2 步指令', '孩子能按照顺序完成两步指令。', '每天 1-2 次', '给予连续两步的简单指令。',
'{"steps": "相关动作", "prompt": "分步提示", "success_criteria": "完成两步"}',
'{"steps": "不相关", "prompt": "说一遍", "success_criteria": "记住顺序"}',
'{"steps": "复杂", "prompt": "无", "success_criteria": "准确完成"}', 37),

('C08', 'cognition', '图形匹配：圆和方', '孩子能区分并匹配圆形和方形。', '每周 3 次', '用图形板或卡片练习匹配。',
'{"shapes": "2种", "prompt": "手把手", "success_criteria": "放入对应"}',
'{"shapes": "3种", "prompt": "语言", "success_criteria": "独立匹配"}',
'{"shapes": "多种", "prompt": "命名", "success_criteria": "说出名称"}', 38),

('C09', 'cognition', '规则游戏：停和走', '孩子能理解"走"和"停"的游戏规则。', '每周多次', '用音乐或口令进行停走游戏。',
'{"prompt": "肢体辅助", "signal": "音乐", "success_criteria": "跟随动作"}',
'{"prompt": "口令", "signal": "语言", "success_criteria": "听令行动"}',
'{"prompt": "多规则", "signal": "复杂", "success_criteria": "规则切换"}', 39),

('C10', 'cognition', '记忆：找一找藏起来的物品', '孩子能记住刚刚藏起的物品位置并找出来。', '每周 3 次', '用藏找游戏训练工作记忆。',
'{"hiding": "当面藏", "delay": "立即", "success_criteria": "找到"}',
'{"hiding": "遮挡藏", "delay": "5秒", "success_criteria": "记住位置"}',
'{"hiding": "多位置", "delay": "10秒", "success_criteria": "准确找到"}', 40);

-- 感觉与运动（G01-G10）
INSERT INTO training_template (code, domain, name, brief_goal, recommended_frequency, base_scenario, level1_rules, level2_rules, level3_rules, sort) VALUES
('G01', 'sensory', '平衡木走 3 步', '孩子能在简单"平衡线"或窄面上走几步。', '每周 3-5 次', '用地上的线或低矮平衡木练习。',
'{"surface": "宽", "support": "双手扶", "success_criteria": "走几步"}',
'{"surface": "窄", "support": "单手", "success_criteria": "独立走"}',
'{"surface": "高", "support": "无", "success_criteria": "稳定走"}', 41),

('G02', 'sensory', '推墙 10 秒', '孩子用力推墙几秒钟，提供本体觉输入。', '每天 1-2 次', '在需要调节时使用推墙活动。',
'{"duration": "5秒", "force": "轻", "success_criteria": "维持姿势"}',
'{"duration": "10秒", "force": "中", "success_criteria": "持续用力"}',
'{"duration": "15秒", "force": "大", "success_criteria": "稳定发力"}', 42),

('G03', 'sensory', '触觉探索：软硬不同的物体', '孩子通过摸不同材质的物品，逐渐适应不同触觉。', '每周 3 次', '提供各种触感材料让孩子探索。',
'{"materials": "喜欢的", "exposure": "短", "success_criteria": "愿意触摸"}',
'{"materials": "多样", "exposure": "中", "success_criteria": "主动探索"}',
'{"materials": "不舒服的", "exposure": "长", "success_criteria": "适应触感"}', 43),

('G04', 'sensory', '双脚原地跳', '孩子能尝试双脚一起离地的小跳。', '每周 3-5 次', '用游戏方式引导原地跳跃。',
'{"support": "扶手", "height": "低", "success_criteria": "双脚离地"}',
'{"support": "无", "height": "中", "success_criteria": "连续跳"}',
'{"support": "无", "height": "高", "success_criteria": "稳定落地"}', 44),

('G05', 'sensory', '跨过小障碍物', '孩子能迈步跨过低矮障碍物。', '每周 3 次', '设置低矮障碍让孩子练习跨越。',
'{"height": "很低", "support": "扶手", "success_criteria": "跨过"}',
'{"height": "中等", "support": "无", "success_criteria": "独立跨"}',
'{"height": "变化", "support": "无", "success_criteria": "灵活跨越"}', 45),

('G06', 'sensory', '精细：把吸管插进洞里', '孩子能把细长的物体准确插入小孔中。', '每周 3-5 次', '用插孔玩具练习手眼协调。',
'{"hole_size": "大", "object": "粗", "success_criteria": "插入"}',
'{"hole_size": "中", "object": "细", "success_criteria": "准确插入"}',
'{"hole_size": "小", "object": "多个", "success_criteria": "快速准确"}', 46),

('G07', 'sensory', '精细：捏夹子夹起物品', '孩子用手指捏夹子夹起小物品。', '每周 3 次', '用晾衣夹等工具练习捏夹。',
'{"clip": "大夹子", "object": "大物", "success_criteria": "夹住"}',
'{"clip": "中夹子", "object": "中物", "success_criteria": "夹起放下"}',
'{"clip": "小夹子", "object": "小物", "success_criteria": "精准操控"}', 47),

('G08', 'sensory', '手眼协调：丢球进桶', '孩子能尝试把球丢进桶里。', '每周 3-5 次', '用大桶和球练习投掷。',
'{"distance": "近", "target": "大桶", "success_criteria": "投进"}',
'{"distance": "中", "target": "中桶", "success_criteria": "准确投"}',
'{"distance": "远", "target": "小桶", "success_criteria": "稳定命中"}', 48),

('G09', 'sensory', '身体认知：摸头摸鼻子', '孩子能在大人指令下摸自己的身体部位。', '每天多次自然带练', '在歌曲或游戏中练习身体认知。',
'{"parts": "头", "prompt": "示范", "success_criteria": "模仿摸"}',
'{"parts": "3个", "prompt": "语言", "success_criteria": "听令摸"}',
'{"parts": "多个", "prompt": "快速", "success_criteria": "准确快速"}', 49),

('G10', 'sensory', '节奏模仿：敲敲停', '孩子能模仿简单的敲击节奏。', '每周多次短游戏', '用敲击游戏训练节奏感。',
'{"rhythm": "简单", "prompt": "示范", "success_criteria": "跟随敲"}',
'{"rhythm": "变化", "prompt": "听觉", "success_criteria": "模仿节奏"}',
'{"rhythm": "复杂", "prompt": "无", "success_criteria": "准确模仿"}', 50);

-- 生活自理（D01-D10）
INSERT INTO training_template (code, domain, name, brief_goal, recommended_frequency, base_scenario, level1_rules, level2_rules, level3_rules, sort) VALUES
('D01', 'daily_living', '洗手三步：开水→搓→冲', '孩子在大人提示下，能按顺序完成洗手三个核心步骤。', '每天至少 1 次', '每次洗手时按步骤引导。',
'{"steps": "1步", "prompt": "手把手", "success_criteria": "配合完成"}',
'{"steps": "3步", "prompt": "语言", "success_criteria": "按顺序做"}',
'{"steps": "完整", "prompt": "无", "success_criteria": "独立洗手"}', 51),

('D02', 'daily_living', '穿鞋：区分左右', '孩子逐渐理解鞋子的左右。', '每天外出前练习', '用标记帮助识别左右鞋。',
'{"marker": "明显标记", "prompt": "示范", "success_criteria": "跟着做"}',
'{"marker": "小标记", "prompt": "提问", "success_criteria": "选对"}',
'{"marker": "无", "prompt": "无", "success_criteria": "自主判断"}', 52),

('D03', 'daily_living', '穿衣：找到袖子洞', '孩子能在大人帮助下找到袖子位置，把手伸进袖子。', '每天穿衣时练习', '穿衣时引导找袖子。',
'{"help": "大人举衣", "prompt": "手把手", "success_criteria": "手伸入"}',
'{"help": "指向", "prompt": "语言", "success_criteria": "找到伸入"}',
'{"help": "无", "prompt": "无", "success_criteria": "独立完成"}', 53),

('D04', 'daily_living', '用勺挖一下吃一口', '孩子能用勺子舀起少量食物并送入口中。', '每天一顿饭中练习', '用餐时练习使用勺子。',
'{"food": "稠的", "help": "手把手", "success_criteria": "送入口"}',
'{"food": "普通", "help": "扶手", "success_criteria": "独立舀吃"}',
'{"food": "各种", "help": "无", "success_criteria": "熟练使用"}', 54),

('D05', 'daily_living', '双手拿杯喝水', '孩子能双手稳定地拿起杯子喝一小口水。', '每天多次', '用餐或喝水时练习。',
'{"cup": "小杯", "water": "少量", "success_criteria": "拿起喝"}',
'{"cup": "普通杯", "water": "适量", "success_criteria": "稳定喝"}',
'{"cup": "各种杯", "water": "满", "success_criteria": "不洒"}', 55),

('D06', 'daily_living', '如厕：坐定 1 分钟', '孩子能在马桶或便盆上稳定坐约 1 分钟。', '每天 1-2 次', '固定时间进行如厕练习。',
'{"duration": "30秒", "distraction": "玩具", "success_criteria": "坐住"}',
'{"duration": "1分钟", "distraction": "少", "success_criteria": "安静坐"}',
'{"duration": "2分钟", "distraction": "无", "success_criteria": "配合如厕"}', 56),

('D07', 'daily_living', '收拾玩具：放回盒子', '在大人带领下，孩子能把散落的玩具收回指定容器。', '每天玩耍结束时', '玩耍后引导收拾。',
'{"toys": "1-2个", "prompt": "带做", "success_criteria": "放入盒子"}',
'{"toys": "多个", "prompt": "语言", "success_criteria": "主动收"}',
'{"toys": "全部", "prompt": "提醒", "success_criteria": "独立收拾"}', 57),

('D08', 'daily_living', '刷牙：张嘴 5 秒', '孩子能在大人操作牙刷时，配合张嘴保持几秒。', '每天 1-2 次', '刷牙时练习配合。',
'{"duration": "3秒", "brush": "手指", "success_criteria": "张嘴"}',
'{"duration": "5秒", "brush": "牙刷", "success_criteria": "保持张嘴"}',
'{"duration": "完整", "brush": "自己刷", "success_criteria": "配合完成"}', 58),

('D09', 'daily_living', '开关灯', '孩子能在指令下自己去开灯或关灯。', '每天自然情境中使用', '利用日常情境练习。',
'{"switch": "易触及", "prompt": "带做", "success_criteria": "完成动作"}',
'{"switch": "普通", "prompt": "语言", "success_criteria": "听令执行"}',
'{"switch": "各种", "prompt": "情境", "success_criteria": "主动做"}', 59),

('D10', 'daily_living', '拉拉链向上 5cm', '孩子能尝试抓住拉链头并向上拉一小段距离。', '每天穿脱外套时练习', '穿脱衣服时练习拉链。',
'{"length": "2cm", "help": "扶住衣服", "success_criteria": "拉动"}',
'{"length": "5cm", "help": "语言", "success_criteria": "独立拉"}',
'{"length": "完整", "help": "无", "success_criteria": "熟练操作"}', 60);

-- 更新序列
SELECT setval('training_template_id_seq', 60, true);
