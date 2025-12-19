package cn.iocoder.yudao.module.member.controller.app.oauth2;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.oauth2.vo.AppOAuth2UserInfoRespVO;
import cn.iocoder.yudao.module.member.convert.oauth2.MemberOAuth2Convert;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - OAuth2 用户信息
 * 
 * 提供给第三方应用（如 NodeBB）通过 OAuth2 获取用户信息
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - OAuth2 用户信息")
@RestController
@RequestMapping("/member/oauth2")
@Validated
@Slf4j
public class AppOAuth2Controller {

    @Resource
    private MemberUserService memberUserService;

    @GetMapping("/user")
    @Operation(summary = "获取 OAuth2 用户信息", description = "需要 access_token 认证")
    public CommonResult<AppOAuth2UserInfoRespVO> getUserInfo() {
        // 1. 获取当前登录用户
        Long userId = getLoginUserId();
        if (userId == null) {
            return success(null);
        }

        // 2. 查询用户信息
        MemberUserDO user = memberUserService.getUser(userId);
        if (user == null) {
            return success(null);
        }

        // 3. 转换并返回
        return success(MemberOAuth2Convert.INSTANCE.convert(user));
    }

}

