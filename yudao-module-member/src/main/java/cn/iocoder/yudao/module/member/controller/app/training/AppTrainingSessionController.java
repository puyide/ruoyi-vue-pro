package cn.iocoder.yudao.module.member.controller.app.training;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.training.vo.*;
import cn.iocoder.yudao.module.member.convert.training.TrainingConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingSessionDO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;
import cn.iocoder.yudao.module.member.service.training.TrainingSessionService;
import cn.iocoder.yudao.module.member.service.training.TrainingTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 训练会话
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - 训练会话")
@RestController
@RequestMapping("/member/training/session")
@Validated
public class AppTrainingSessionController {

    @Resource
    private TrainingSessionService trainingSessionService;

    @Resource
    private TrainingTemplateService trainingTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建训练会话（生成训练卡片）")
    public CommonResult<Long> createSession(@Valid @RequestBody AppTrainingSessionCreateReqVO createReqVO) {
        return success(trainingSessionService.createSession(getLoginUserId(), createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得训练会话详情")
    @Parameter(name = "id", description = "会话编号", required = true, example = "1")
    public CommonResult<AppTrainingSessionDetailRespVO> getSession(@RequestParam("id") Long id) {
        TrainingSessionDO session = trainingSessionService.getSession(getLoginUserId(), id);
        if (session == null) {
            return success(null);
        }
        TrainingTemplateDO template = trainingTemplateService.getTemplate(session.getTemplateId());
        return success(TrainingConvert.INSTANCE.convertSessionDetail(session, template));
    }

    @GetMapping("/today-list")
    @Operation(summary = "获得今日训练列表")
    @Parameter(name = "childId", description = "孩子ID", required = true, example = "1")
    public CommonResult<List<AppTrainingSessionRespVO>> getTodaySessionList(
            @RequestParam("childId") Long childId) {
        List<TrainingSessionDO> sessions = trainingSessionService.getTodaySessionList(getLoginUserId(), childId);
        Map<Long, TrainingTemplateDO> templateMap = getTemplateMap(sessions);
        return success(TrainingConvert.INSTANCE.convertSessionList(sessions, templateMap));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询训练会话")
    public CommonResult<PageResult<AppTrainingSessionRespVO>> getSessionPage(@Valid AppTrainingSessionPageReqVO pageReqVO) {
        PageResult<TrainingSessionDO> pageResult = trainingSessionService.getSessionPage(getLoginUserId(), pageReqVO);
        Map<Long, TrainingTemplateDO> templateMap = getTemplateMap(pageResult.getList());
        List<AppTrainingSessionRespVO> voList = TrainingConvert.INSTANCE.convertSessionList(pageResult.getList(), templateMap);
        return success(new PageResult<>(voList, pageResult.getTotal()));
    }

    @PutMapping("/start")
    @Operation(summary = "开始训练会话")
    @Parameter(name = "id", description = "会话编号", required = true, example = "1")
    public CommonResult<Boolean> startSession(@RequestParam("id") Long id) {
        trainingSessionService.startSession(getLoginUserId(), id);
        return success(true);
    }

    @PutMapping("/skip")
    @Operation(summary = "跳过训练会话")
    @Parameter(name = "id", description = "会话编号", required = true, example = "1")
    public CommonResult<Boolean> skipSession(@RequestParam("id") Long id) {
        trainingSessionService.skipSession(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/today-progress")
    @Operation(summary = "获取今日训练进度")
    @Parameter(name = "childId", description = "孩子ID", required = true, example = "1")
    public CommonResult<AppTrainingProgressRespVO> getTodayProgress(@RequestParam("childId") Long childId) {
        TrainingSessionService.TodayProgress progress = trainingSessionService.getTodayProgress(getLoginUserId(), childId);
        
        AppTrainingProgressRespVO vo = new AppTrainingProgressRespVO();
        vo.setTodayTotal(progress.total());
        vo.setTodayCompleted(progress.completed());
        vo.setTodayTotalMinutes(progress.totalMinutes());
        vo.setTodayProgressPercent(progress.total() > 0 ? progress.completed() * 100 / progress.total() : 0);
        
        // 鼓励文案
        if (progress.totalMinutes() > 0) {
            vo.setEncouragement(String.format("陪孩子练了 %d 分钟，已经很棒了 👍", progress.totalMinutes()));
        } else if (progress.completed() > 0) {
            vo.setEncouragement(String.format("今天已完成 %d 个训练，继续加油！", progress.completed()));
        } else {
            vo.setEncouragement("开始今天的第一个训练吧！");
        }
        
        return success(vo);
    }

    /**
     * 获取模板 Map
     */
    private Map<Long, TrainingTemplateDO> getTemplateMap(List<TrainingSessionDO> sessions) {
        Set<Long> templateIds = sessions.stream()
                .map(TrainingSessionDO::getTemplateId)
                .collect(Collectors.toSet());
        List<TrainingTemplateDO> templates = trainingTemplateService.getActiveTemplateList().stream()
                .filter(t -> templateIds.contains(t.getId()))
                .toList();
        return templates.stream()
                .collect(Collectors.toMap(TrainingTemplateDO::getId, Function.identity()));
    }

}

