package cn.iocoder.yudao.module.member.convert.training;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.member.controller.app.training.vo.*;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingLogDO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingSessionDO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;
import cn.iocoder.yudao.module.member.enums.training.ChildMoodEnum;
import cn.iocoder.yudao.module.member.enums.training.TrainingDifficultyLevelEnum;
import cn.iocoder.yudao.module.member.enums.training.TrainingDomainEnum;
import cn.iocoder.yudao.module.member.enums.training.TrainingSessionStatusEnum;
import cn.iocoder.yudao.module.member.service.training.TrainingLogService;
import cn.iocoder.yudao.module.member.service.training.TrainingTemplateService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 训练模块 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface TrainingConvert {

    TrainingConvert INSTANCE = Mappers.getMapper(TrainingConvert.class);

    // ========== 模板转换 ==========

    @Mapping(target = "domainName", source = "domain", qualifiedByName = "domainToName")
    AppTrainingTemplateRespVO convertTemplate(TrainingTemplateDO bean);

    List<AppTrainingTemplateRespVO> convertTemplateList(List<TrainingTemplateDO> list);

    // ========== 域统计转换 ==========

    default AppTrainingDomainRespVO convertDomainStatistics(TrainingTemplateService.DomainStatistics stat) {
        AppTrainingDomainRespVO vo = new AppTrainingDomainRespVO();
        vo.setDomain(stat.domain());
        vo.setDomainName(stat.domainName());
        vo.setCount(stat.count());
        vo.setIcon(getDomainIcon(stat.domain()));
        vo.setDescription(getDomainDescription(stat.domain()));
        return vo;
    }

    default List<AppTrainingDomainRespVO> convertDomainStatisticsList(List<TrainingTemplateService.DomainStatistics> list) {
        return list.stream().map(this::convertDomainStatistics).toList();
    }

    // ========== 会话转换 ==========

    default AppTrainingSessionRespVO convertSession(TrainingSessionDO session, TrainingTemplateDO template) {
        AppTrainingSessionRespVO vo = new AppTrainingSessionRespVO();
        vo.setId(session.getId());
        vo.setChildId(session.getChildId());
        vo.setTemplateId(session.getTemplateId());
        vo.setDifficultyLevel(session.getDifficultyLevel());
        vo.setScheduledAt(session.getScheduledAt());
        vo.setStatus(session.getStatus());
        vo.setActualDurationMinutes(session.getActualDurationMinutes());
        vo.setStartedAt(session.getStartedAt());
        vo.setCompletedAt(session.getCompletedAt());
        vo.setCreateTime(session.getCreateTime());

        // 模板信息
        if (template != null) {
            vo.setTemplateCode(template.getCode());
            vo.setTemplateName(template.getName());
            vo.setDomain(template.getDomain());
            vo.setDomainName(domainToName(template.getDomain()));
            vo.setEstimatedDurationMinutes(5); // 默认5分钟
        }

        // 难度等级名称
        TrainingDifficultyLevelEnum levelEnum = TrainingDifficultyLevelEnum.getByLevel(session.getDifficultyLevel());
        if (levelEnum != null) {
            vo.setDifficultyLevelName(levelEnum.getCode() + " " + levelEnum.getName());
        }

        // 状态名称
        TrainingSessionStatusEnum statusEnum = TrainingSessionStatusEnum.getByStatus(session.getStatus());
        if (statusEnum != null) {
            vo.setStatusName(statusEnum.getName());
        }

        return vo;
    }

    default List<AppTrainingSessionRespVO> convertSessionList(List<TrainingSessionDO> sessions, 
                                                               Map<Long, TrainingTemplateDO> templateMap) {
        return sessions.stream()
                .map(s -> convertSession(s, templateMap.get(s.getTemplateId())))
                .toList();
    }

    // ========== 会话详情转换 ==========

    default AppTrainingSessionDetailRespVO convertSessionDetail(TrainingSessionDO session, TrainingTemplateDO template) {
        AppTrainingSessionDetailRespVO vo = new AppTrainingSessionDetailRespVO();
        // 复制基础字段
        AppTrainingSessionRespVO baseVo = convertSession(session, template);
        vo.setId(baseVo.getId());
        vo.setChildId(baseVo.getChildId());
        vo.setTemplateId(baseVo.getTemplateId());
        vo.setTemplateCode(baseVo.getTemplateCode());
        vo.setTemplateName(baseVo.getTemplateName());
        vo.setDomain(baseVo.getDomain());
        vo.setDomainName(baseVo.getDomainName());
        vo.setDifficultyLevel(baseVo.getDifficultyLevel());
        vo.setDifficultyLevelName(baseVo.getDifficultyLevelName());
        vo.setScheduledAt(baseVo.getScheduledAt());
        vo.setStatus(baseVo.getStatus());
        vo.setStatusName(baseVo.getStatusName());
        vo.setEstimatedDurationMinutes(baseVo.getEstimatedDurationMinutes());
        vo.setActualDurationMinutes(baseVo.getActualDurationMinutes());
        vo.setStartedAt(baseVo.getStartedAt());
        vo.setCompletedAt(baseVo.getCompletedAt());
        vo.setCreateTime(baseVo.getCreateTime());

        // 模板信息
        if (template != null) {
            vo.setBriefGoal(template.getBriefGoal());
            vo.setRecommendedFrequency(template.getRecommendedFrequency());
        }

        // 解析 AI 生成的训练卡片
        if (StrUtil.isNotBlank(session.getAiOutputJson())) {
            try {
                vo.setTrainingCard(JSONUtil.toBean(session.getAiOutputJson(), 
                        AppTrainingSessionDetailRespVO.TrainingCardVO.class));
            } catch (Exception ignored) {
            }
        }

        return vo;
    }

    // ========== 日志转换 ==========

    default AppTrainingLogRespVO convertLog(TrainingLogDO log, TrainingTemplateDO template) {
        AppTrainingLogRespVO vo = new AppTrainingLogRespVO();
        vo.setId(log.getId());
        vo.setSessionId(log.getSessionId());
        vo.setChildId(log.getChildId());
        vo.setTemplateId(log.getTemplateId());
        vo.setDomain(log.getDomain());
        vo.setDomainName(domainToName(log.getDomain()));
        vo.setDoneAt(log.getDoneAt());
        vo.setCompleted(log.getCompleted());
        vo.setChildMood(log.getChildMood());
        vo.setSuccessCount(log.getSuccessCount());
        vo.setParentComment(log.getParentComment());

        // 模板名称
        if (template != null) {
            vo.setTemplateName(template.getName());
        }

        // 情绪名称和表情
        ChildMoodEnum moodEnum = ChildMoodEnum.getByValue(log.getChildMood());
        if (moodEnum != null) {
            vo.setChildMoodName(moodEnum.getName());
            vo.setChildMoodEmoji(moodEnum.getEmoji());
        }

        // 困难标签
        if (StrUtil.isNotBlank(log.getDifficulties())) {
            try {
                vo.setDifficulties(JSONUtil.toList(log.getDifficulties(), String.class));
            } catch (Exception e) {
                vo.setDifficulties(Collections.emptyList());
            }
        }

        return vo;
    }

    default List<AppTrainingLogRespVO> convertLogList(List<TrainingLogDO> logs, 
                                                       Map<Long, TrainingTemplateDO> templateMap) {
        return logs.stream()
                .map(l -> convertLog(l, templateMap.get(l.getTemplateId())))
                .toList();
    }

    // ========== 反馈转换 ==========

    default AppTrainingFeedbackRespVO convertFeedback(TrainingLogService.AiFeedback feedback) {
        AppTrainingFeedbackRespVO vo = new AppTrainingFeedbackRespVO();
        vo.setAffirmation(feedback.affirmation());
        vo.setSummary(feedback.summary());
        vo.setNextSuggestion(feedback.nextSuggestion());
        vo.setSuggestedLevel(feedback.suggestedLevel());

        // 建议等级说明
        TrainingDifficultyLevelEnum levelEnum = TrainingDifficultyLevelEnum.getByLevel(feedback.suggestedLevel());
        if (levelEnum != null) {
            vo.setSuggestedLevelHint("可以尝试 " + levelEnum.getCode() + " " + levelEnum.getName() + "训练");
        }

        return vo;
    }

    // ========== 工具方法 ==========
    // 注意: 所有辅助方法都需要 @Named 注解，避免 MapStruct 将其误认为 String→String 映射方法

    @Named("domainToName")
    default String domainToName(String domain) {
        TrainingDomainEnum domainEnum = TrainingDomainEnum.getByCode(domain);
        return domainEnum != null ? domainEnum.getName() : domain;
    }

    @Named("domainToIcon")
    default String getDomainIcon(String domain) {
        return switch (domain) {
            case "social" -> "icon-social";
            case "language" -> "icon-language";
            case "emotion" -> "icon-emotion";
            case "cognition" -> "icon-cognition";
            case "sensory" -> "icon-sensory";
            case "daily_living" -> "icon-daily";
            default -> "icon-training";
        };
    }

    @Named("domainToDescription")
    default String getDomainDescription(String domain) {
        return switch (domain) {
            case "social" -> "训练孩子的社交互动能力，包括眼神接触、轮流、模仿等";
            case "language" -> "训练孩子的语言表达和理解能力";
            case "emotion" -> "训练孩子的情绪认知和行为管理";
            case "cognition" -> "训练孩子的认知能力和规则理解";
            case "sensory" -> "训练孩子的感觉统合和运动能力";
            case "daily_living" -> "训练孩子的日常生活自理能力";
            default -> "训练项目";
        };
    }

}

