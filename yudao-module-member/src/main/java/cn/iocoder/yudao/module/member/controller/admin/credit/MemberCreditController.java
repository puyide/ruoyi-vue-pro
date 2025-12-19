package cn.iocoder.yudao.module.member.controller.admin.credit;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.credit.vo.CreditLogPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.credit.vo.CreditScoreAdjustReqVO;
import cn.iocoder.yudao.module.member.controller.admin.credit.vo.CreditScorePageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.credit.vo.CreditScoreRespVO;
import cn.iocoder.yudao.module.member.controller.admin.credit.vo.CreditLogRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditLogDO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditScoreDO;
import cn.iocoder.yudao.module.member.service.credit.MemberCreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 会员信用分
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 会员信用分")
@RestController
@RequestMapping("/member/credit/admin")
@Validated
public class MemberCreditController {

    @Resource
    private MemberCreditService memberCreditService;

    @GetMapping("/score/page")
    @Operation(summary = "获取信用分分页")
    @PreAuthorize("@ss.hasPermission('member:credit:query')")
    public CommonResult<PageResult<CreditScoreRespVO>> getCreditScorePage(@Valid CreditScorePageReqVO pageReqVO) {
        // TODO: 实现分页查询，需要关联用户信息
        return success(new PageResult<>());
    }

    @GetMapping("/score/get")
    @Operation(summary = "获取用户信用分")
    @Parameter(name = "userId", description = "用户ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:credit:query')")
    public CommonResult<CreditScoreRespVO> getUserCreditScore(@RequestParam("userId") Long userId) {
        MemberCreditScoreDO creditScore = memberCreditService.getOrInitCreditScore(userId);
        CreditScoreRespVO respVO = new CreditScoreRespVO();
        respVO.setId(creditScore.getId());
        respVO.setUserId(creditScore.getUserId());
        respVO.setTotalScore(creditScore.getTotalScore());
        respVO.setDonateCount(creditScore.getDonateCount());
        respVO.setBorrowCount(creditScore.getBorrowCount());
        respVO.setReturnOnTimeCount(creditScore.getReturnOnTimeCount());
        respVO.setOverdueCount(creditScore.getOverdueCount());
        respVO.setDamageCount(creditScore.getDamageCount());
        respVO.setViolationCount(creditScore.getViolationCount());
        respVO.setScoreChangeReason(creditScore.getScoreChangeReason());
        respVO.setLastScoreChangeDate(creditScore.getLastScoreChangeDate());
        return success(respVO);
    }

    @PostMapping("/score/adjust")
    @Operation(summary = "调整用户信用分")
    @PreAuthorize("@ss.hasPermission('member:credit:update')")
    public CommonResult<Boolean> adjustCreditScore(@Valid @RequestBody CreditScoreAdjustReqVO reqVO) {
        if (reqVO.getChangeScore() > 0) {
            memberCreditService.addCreditScore(reqVO.getUserId(), reqVO.getChangeType(), 
                    reqVO.getChangeScore(), reqVO.getReason(), null);
        } else {
            memberCreditService.reduceCreditScore(reqVO.getUserId(), reqVO.getChangeType(), 
                    Math.abs(reqVO.getChangeScore()), reqVO.getReason(), null);
        }
        return success(true);
    }

    @GetMapping("/log/page")
    @Operation(summary = "获取信用分变动记录分页")
    @PreAuthorize("@ss.hasPermission('member:credit:query')")
    public CommonResult<PageResult<MemberCreditLogDO>> getCreditLogPage(@Valid CreditLogPageReqVO pageReqVO) {
        PageResult<MemberCreditLogDO> pageResult = memberCreditService.getCreditLogPage(pageReqVO);
        return success(pageResult);
    }

}

