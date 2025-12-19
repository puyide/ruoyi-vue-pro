package cn.iocoder.yudao.module.member.convert.peergroup;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.*;
import cn.iocoder.yudao.module.member.controller.app.peergroup.vo.*;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupMemberDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 同行小组 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface PeerGroupConvert {

    PeerGroupConvert INSTANCE = Mappers.getMapper(PeerGroupConvert.class);

    // ========== 管理后台 ==========

    PeerGroupDO convert(PeerGroupCreateReqVO bean);

    PeerGroupDO convert(PeerGroupUpdateReqVO bean);

    PeerGroupRespVO convert(PeerGroupDO bean);

    List<PeerGroupRespVO> convertList(List<PeerGroupDO> list);

    PageResult<PeerGroupRespVO> convertPage(PageResult<PeerGroupDO> page);

    // ========== 小程序端 ==========

    AppPeerGroupRespVO convertToApp(PeerGroupDO bean);

    List<AppPeerGroupRespVO> convertToAppList(List<PeerGroupDO> list);

    AppPeerGroupDetailRespVO convertToAppDetail(PeerGroupDO bean);

    AppPeerGroupMemberRespVO convertToAppMember(PeerGroupMemberDO bean);

}

