package cn.iocoder.yudao.module.member.controller.app.training;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingDomainRespVO;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingTemplateRespVO;
import cn.iocoder.yudao.module.member.convert.training.TrainingConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;
import cn.iocoder.yudao.module.member.service.training.TrainingTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 训练模板
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - 训练模板")
@RestController
@RequestMapping("/member/training/template")
@Validated
public class AppTrainingTemplateController {

    @Resource
    private TrainingTemplateService trainingTemplateService;

    @GetMapping("/list")
    @Operation(summary = "获得训练模板列表")
    @Parameter(name = "domain", description = "训练域", example = "social")
    public CommonResult<List<AppTrainingTemplateRespVO>> getTemplateList(
            @RequestParam(value = "domain", required = false) String domain) {
        List<TrainingTemplateDO> list;
        if (domain != null && !domain.isEmpty()) {
            list = trainingTemplateService.getTemplateListByDomain(domain);
        } else {
            list = trainingTemplateService.getActiveTemplateList();
        }
        return success(TrainingConvert.INSTANCE.convertTemplateList(list));
    }

    @GetMapping("/get")
    @Operation(summary = "获得训练模板详情")
    @Parameter(name = "id", description = "模板编号", required = true, example = "1")
    public CommonResult<AppTrainingTemplateRespVO> getTemplate(@RequestParam("id") Long id) {
        TrainingTemplateDO template = trainingTemplateService.getTemplate(id);
        return success(TrainingConvert.INSTANCE.convertTemplate(template));
    }

    @GetMapping("/domains")
    @Operation(summary = "获得训练域列表（含模板数量）")
    public CommonResult<List<AppTrainingDomainRespVO>> getDomainList() {
        List<TrainingTemplateService.DomainStatistics> statistics = trainingTemplateService.getDomainStatistics();
        return success(TrainingConvert.INSTANCE.convertDomainStatisticsList(statistics));
    }

}

