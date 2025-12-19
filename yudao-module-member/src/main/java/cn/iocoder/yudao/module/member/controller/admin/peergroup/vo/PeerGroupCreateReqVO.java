package cn.iocoder.yudao.module.member.controller.admin.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 同行小组创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PeerGroupCreateReqVO extends PeerGroupBaseVO {

}

