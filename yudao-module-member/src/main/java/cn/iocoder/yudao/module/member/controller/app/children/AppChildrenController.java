package cn.iocoder.yudao.module.member.controller.app.children;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenRespVO;
import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenUpdateReqVO;
import cn.iocoder.yudao.module.member.convert.children.ChildrenConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.children.MemberChildrenDO;
import cn.iocoder.yudao.module.member.service.children.ChildrenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 儿童个人信息
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - 儿童个人信息")
@RestController
@RequestMapping("/member/children")
@Validated
public class AppChildrenController {

    @Resource
    private ChildrenService childrenService;

    @PostMapping("/create")
    @Operation(summary = "创建儿童信息")
    public CommonResult<Long> createChildren(@Valid @RequestBody AppChildrenCreateReqVO createReqVO) {
        return success(childrenService.createChildren(getLoginUserId(), createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新儿童信息")
    public CommonResult<Boolean> updateChildren(@Valid @RequestBody AppChildrenUpdateReqVO updateReqVO) {
        childrenService.updateChildren(getLoginUserId(), updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除儿童信息")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteChildren(@RequestParam("id") Long id) {
        childrenService.deleteChildren(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得儿童信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppChildrenRespVO> getChildren(@RequestParam("id") Long id) {
        MemberChildrenDO children = childrenService.getChildren(getLoginUserId(), id);
        return success(ChildrenConvert.INSTANCE.convert(children));
    }

    @GetMapping("/list")
    @Operation(summary = "获得儿童信息列表")
    public CommonResult<List<AppChildrenRespVO>> getChildrenList() {
        List<MemberChildrenDO> list = childrenService.getChildrenList(getLoginUserId());
        return success(ChildrenConvert.INSTANCE.convertList(list));
    }

}

