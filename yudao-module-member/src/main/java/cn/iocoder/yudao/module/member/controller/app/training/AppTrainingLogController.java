package cn.iocoder.yudao.module.member.controller.app.training;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.training.vo.*;
import cn.iocoder.yudao.module.member.convert.training.TrainingConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingLogDO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;
import cn.iocoder.yudao.module.member.service.training.TrainingLogService;
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
 * 用户 APP - 训练日志（打卡）
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - 训练日志（打卡）")
@RestController
@RequestMapping("/member/training/log")
@Validated
public class AppTrainingLogController {

    @Resource
    private TrainingLogService trainingLogService;

    @Resource
    private TrainingTemplateService trainingTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建训练日志（打卡）")
    public CommonResult<Long> createLog(@Valid @RequestBody AppTrainingLogCreateReqVO createReqVO) {
        return success(trainingLogService.createLog(getLoginUserId(), createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得训练日志")
    @Parameter(name = "id", description = "日志编号", required = true, example = "1")
    public CommonResult<AppTrainingLogRespVO> getLog(@RequestParam("id") Long id) {
        TrainingLogDO log = trainingLogService.getLog(getLoginUserId(), id);
        if (log == null) {
            return success(null);
        }
        TrainingTemplateDO template = trainingTemplateService.getTemplate(log.getTemplateId());
        return success(TrainingConvert.INSTANCE.convertLog(log, template));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询训练日志")
    public CommonResult<PageResult<AppTrainingLogRespVO>> getLogPage(@Valid AppTrainingLogPageReqVO pageReqVO) {
        PageResult<TrainingLogDO> pageResult = trainingLogService.getLogPage(getLoginUserId(), pageReqVO);
        Map<Long, TrainingTemplateDO> templateMap = getTemplateMap(pageResult.getList());
        List<AppTrainingLogRespVO> voList = TrainingConvert.INSTANCE.convertLogList(pageResult.getList(), templateMap);
        return success(new PageResult<>(voList, pageResult.getTotal()));
    }

    @GetMapping("/feedback")
    @Operation(summary = "获取 AI 训练反馈")
    @Parameter(name = "logId", description = "日志编号", required = true, example = "1")
    public CommonResult<AppTrainingFeedbackRespVO> getFeedback(@RequestParam("logId") Long logId) {
        TrainingLogService.AiFeedback feedback = trainingLogService.generateAiFeedback(getLoginUserId(), logId);
        return success(TrainingConvert.INSTANCE.convertFeedback(feedback));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取训练统计信息")
    @Parameter(name = "childId", description = "孩子ID", required = true, example = "1")
    public CommonResult<AppTrainingProgressRespVO> getStatistics(@RequestParam("childId") Long childId) {
        TrainingLogService.TrainingStatistics stats = trainingLogService.getStatistics(getLoginUserId(), childId);
        
        AppTrainingProgressRespVO vo = new AppTrainingProgressRespVO();
        vo.setTotalSessions(stats.totalSessions());
        vo.setTrainingDays(stats.trainingDays());
        vo.setConsecutiveDays(stats.consecutiveDays());
        vo.setAvgSuccessRatePercent((int) (stats.avgSuccessRate() * 100));
        
        // 鼓励文案
        if (stats.consecutiveDays() >= 7) {
            vo.setEncouragement(String.format("太棒了！已连续 %d 天训练，坚持就是胜利！", stats.consecutiveDays()));
        } else if (stats.totalSessions() >= 10) {
            vo.setEncouragement(String.format("已完成 %d 次训练，小台阶正在慢慢垒起来。", stats.totalSessions()));
        } else {
            vo.setEncouragement("每一次练习都是进步的开始。");
        }
        
        return success(vo);
    }

    /**
     * 获取模板 Map
     */
    private Map<Long, TrainingTemplateDO> getTemplateMap(List<TrainingLogDO> logs) {
        Set<Long> templateIds = logs.stream()
                .map(TrainingLogDO::getTemplateId)
                .collect(Collectors.toSet());
        List<TrainingTemplateDO> templates = trainingTemplateService.getActiveTemplateList().stream()
                .filter(t -> templateIds.contains(t.getId()))
                .toList();
        return templates.stream()
                .collect(Collectors.toMap(TrainingTemplateDO::getId, Function.identity()));
    }

}

