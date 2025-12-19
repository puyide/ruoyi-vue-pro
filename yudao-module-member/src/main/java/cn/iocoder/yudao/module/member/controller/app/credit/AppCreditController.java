package cn.iocoder.yudao.module.member.controller.app.credit;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.credit.vo.AppCreditLogRespVO;
import cn.iocoder.yudao.module.member.controller.app.credit.vo.AppCreditScoreRespVO;
import cn.iocoder.yudao.module.member.convert.credit.MemberCreditConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditLogDO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditScoreDO;
import cn.iocoder.yudao.module.member.service.credit.MemberCreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 信用分
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - 信用分")
@RestController
@RequestMapping("/member/credit")
@Validated
public class AppCreditController {

    @Resource
    private MemberCreditService memberCreditService;

    @GetMapping("/score")
    @Operation(summary = "获取我的信用分")
    public CommonResult<AppCreditScoreRespVO> getMyCreditScore() {
        MemberCreditScoreDO creditScore = memberCreditService.getOrInitCreditScore(getLoginUserId());
        return success(MemberCreditConvert.INSTANCE.convert(creditScore));
    }

    @GetMapping("/log/list")
    @Operation(summary = "获取我的信用分变动记录")
    @Parameter(name = "limit", description = "限制数量", example = "10")
    public CommonResult<List<AppCreditLogRespVO>> getMyCreditLogList(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<MemberCreditLogDO> list = memberCreditService.getCreditLogList(getLoginUserId(), limit);
        return success(MemberCreditConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/check")
    @Operation(summary = "检查信用分是否满足要求")
    @Parameter(name = "minScore", description = "最低信用分要求", required = true, example = "60")
    public CommonResult<Boolean> checkCreditScore(@RequestParam("minScore") Integer minScore) {
        return success(memberCreditService.checkCreditScore(getLoginUserId(), minScore));
    }

}

