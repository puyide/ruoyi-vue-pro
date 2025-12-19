package cn.iocoder.yudao.module.member.convert.oauth2;

import cn.iocoder.yudao.module.member.controller.app.oauth2.vo.AppOAuth2UserInfoRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * OAuth2 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberOAuth2Convert {

    MemberOAuth2Convert INSTANCE = Mappers.getMapper(MemberOAuth2Convert.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "nickname", target = "username")  // NodeBB 需要 username，映射为 nickname
    @Mapping(source = "nickname", target = "nickname")
    @Mapping(target = "email", expression = "java(user.getMobile() != null ? user.getMobile() + \"@member.local\" : null)")  // 如果没有邮箱，生成临时邮箱
    @Mapping(source = "mobile", target = "mobile")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "name", target = "name")
    AppOAuth2UserInfoRespVO convert(MemberUserDO user);

}

