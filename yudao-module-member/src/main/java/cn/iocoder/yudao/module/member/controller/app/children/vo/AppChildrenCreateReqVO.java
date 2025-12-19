package cn.iocoder.yudao.module.member.controller.app.children.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 用户 APP - 儿童个人信息创建 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "用户 APP - 儿童个人信息创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppChildrenCreateReqVO extends AppChildrenBaseVO {

}

