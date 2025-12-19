package cn.iocoder.yudao.module.member.service.training;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingSessionCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingSessionPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.children.MemberChildrenDO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingSessionDO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;
import cn.iocoder.yudao.module.member.dal.mysql.training.TrainingSessionMapper;
import cn.iocoder.yudao.module.member.enums.training.TrainingSessionStatusEnum;
import cn.iocoder.yudao.module.member.service.children.ChildrenService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.*;

/**
 * 训练会话 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class TrainingSessionServiceImpl implements TrainingSessionService {

    @Resource
    private TrainingSessionMapper trainingSessionMapper;

    @Resource
    private TrainingTemplateService trainingTemplateService;

    @Resource
    private ChildrenService childrenService;

    @Override
    public Long createSession(Long userId, AppTrainingSessionCreateReqVO createReqVO) {
        // 1. 校验孩子存在
        MemberChildrenDO child = childrenService.getChildren(userId, createReqVO.getChildId());
        if (child == null) {
            throw exception(CHILDREN_NOT_EXISTS);
        }

        // 2. 校验模板存在
        TrainingTemplateDO template = trainingTemplateService.getTemplate(createReqVO.getTemplateId());
        if (template == null) {
            throw exception(TRAINING_TEMPLATE_NOT_EXISTS);
        }

        // 3. 构建 AI 输入 JSON
        String aiInputJson = buildAiInputJson(child, template, createReqVO);

        // 4. 创建会话
        TrainingSessionDO session = TrainingSessionDO.builder()
                .childId(createReqVO.getChildId())
                .userId(userId)
                .templateId(createReqVO.getTemplateId())
                .scheduledAt(createReqVO.getScheduledAt() != null ? createReqVO.getScheduledAt() : LocalDateTime.now())
                .difficultyLevel(createReqVO.getDifficultyLevel() != null ? createReqVO.getDifficultyLevel() : 1)
                .aiInputJson(aiInputJson)
                .aiOutputJson(null) // AI 输出将在调用 AI 后填充
                .status(TrainingSessionStatusEnum.PLANNED.getStatus())
                .build();

        trainingSessionMapper.insert(session);
        return session.getId();
    }

    @Override
    public TrainingSessionDO getSession(Long userId, Long id) {
        return trainingSessionMapper.selectByIdAndUserId(id, userId);
    }

    @Override
    public List<TrainingSessionDO> getTodaySessionList(Long userId, Long childId) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return trainingSessionMapper.selectTodayListByChildId(childId, userId, todayStart, todayEnd);
    }

    @Override
    public PageResult<TrainingSessionDO> getSessionPage(Long userId, AppTrainingSessionPageReqVO pageReqVO) {
        return trainingSessionMapper.selectPage(userId, pageReqVO.getChildId(), pageReqVO.getStatus(),
                pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public void startSession(Long userId, Long id) {
        TrainingSessionDO session = validateSessionExists(userId, id);
        if (!TrainingSessionStatusEnum.PLANNED.getStatus().equals(session.getStatus())) {
            throw exception(TRAINING_SESSION_STATUS_ERROR);
        }

        TrainingSessionDO updateObj = new TrainingSessionDO();
        updateObj.setId(id);
        updateObj.setStatus(TrainingSessionStatusEnum.IN_PROGRESS.getStatus());
        updateObj.setStartedAt(LocalDateTime.now());
        trainingSessionMapper.updateById(updateObj);
    }

    @Override
    public void completeSession(Long userId, Long id, Integer actualDurationMinutes) {
        TrainingSessionDO session = validateSessionExists(userId, id);
        if (!TrainingSessionStatusEnum.IN_PROGRESS.getStatus().equals(session.getStatus()) &&
            !TrainingSessionStatusEnum.PLANNED.getStatus().equals(session.getStatus())) {
            throw exception(TRAINING_SESSION_STATUS_ERROR);
        }

        TrainingSessionDO updateObj = new TrainingSessionDO();
        updateObj.setId(id);
        updateObj.setStatus(TrainingSessionStatusEnum.DONE.getStatus());
        updateObj.setCompletedAt(LocalDateTime.now());
        updateObj.setActualDurationMinutes(actualDurationMinutes);
        trainingSessionMapper.updateById(updateObj);
    }

    @Override
    public void skipSession(Long userId, Long id) {
        TrainingSessionDO session = validateSessionExists(userId, id);
        if (TrainingSessionStatusEnum.DONE.getStatus().equals(session.getStatus())) {
            throw exception(TRAINING_SESSION_STATUS_ERROR);
        }

        TrainingSessionDO updateObj = new TrainingSessionDO();
        updateObj.setId(id);
        updateObj.setStatus(TrainingSessionStatusEnum.SKIPPED.getStatus());
        trainingSessionMapper.updateById(updateObj);
    }

    @Override
    public TodayProgress getTodayProgress(Long userId, Long childId) {
        List<TrainingSessionDO> todaySessions = getTodaySessionList(userId, childId);
        int total = todaySessions.size();
        int completed = (int) todaySessions.stream()
                .filter(s -> TrainingSessionStatusEnum.DONE.getStatus().equals(s.getStatus()))
                .count();
        int totalMinutes = todaySessions.stream()
                .filter(s -> s.getActualDurationMinutes() != null)
                .mapToInt(TrainingSessionDO::getActualDurationMinutes)
                .sum();
        return new TodayProgress(total, completed, totalMinutes);
    }

    /**
     * 校验会话存在
     */
    private TrainingSessionDO validateSessionExists(Long userId, Long id) {
        TrainingSessionDO session = getSession(userId, id);
        if (session == null) {
            throw exception(TRAINING_SESSION_NOT_EXISTS);
        }
        return session;
    }

    /**
     * 构建 AI 输入 JSON
     */
    private String buildAiInputJson(MemberChildrenDO child, TrainingTemplateDO template, 
                                     AppTrainingSessionCreateReqVO createReqVO) {
        Map<String, Object> input = new HashMap<>();

        // 孩子档案
        Map<String, Object> childProfile = new HashMap<>();
        childProfile.put("age", child.getAgeYears());
        childProfile.put("gender", child.getGender());
        childProfile.put("age_group", child.getAgeGroup());
        if (StrUtil.isNotBlank(child.getStatusTags())) {
            childProfile.put("status_tags", JSONUtil.parseArray(child.getStatusTags()));
        }
        if (StrUtil.isNotBlank(child.getGoalTags())) {
            childProfile.put("goal_tags", JSONUtil.parseArray(child.getGoalTags()));
        }
        if (StrUtil.isNotBlank(child.getDescription())) {
            childProfile.put("notes", child.getDescription());
        }
        input.put("child_profile", childProfile);

        // 环境信息（从请求中获取，或使用默认值）
        Map<String, Object> environment = new HashMap<>();
        if (createReqVO.getEnvironment() != null) {
            environment.putAll(createReqVO.getEnvironment());
        } else {
            environment.put("available_rooms", List.of("客厅", "卧室"));
            environment.put("time_limit_minutes", 10);
        }
        input.put("environment", environment);

        // 模板信息
        Map<String, Object> templateInfo = new HashMap<>();
        templateInfo.put("id", template.getId());
        templateInfo.put("code", template.getCode());
        templateInfo.put("domain", template.getDomain());
        templateInfo.put("name", template.getName());
        templateInfo.put("brief_goal", template.getBriefGoal());
        templateInfo.put("base_scenario", template.getBaseScenario());
        templateInfo.put("recommended_frequency", template.getRecommendedFrequency());
        input.put("template", templateInfo);

        // 难度等级
        input.put("difficulty_level", createReqVO.getDifficultyLevel() != null ? createReqVO.getDifficultyLevel() : 1);

        return JSONUtil.toJsonStr(input);
    }

}

