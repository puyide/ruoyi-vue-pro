package cn.iocoder.yudao.module.member.convert.children;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenRespVO;
import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenUpdateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.children.MemberChildrenDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

/**
 * 儿童个人信息 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ChildrenConvert {

    ChildrenConvert INSTANCE = Mappers.getMapper(ChildrenConvert.class);

    @Mapping(source = "statusTags", target = "statusTags", qualifiedByName = "listToJson")
    @Mapping(source = "goalTags", target = "goalTags", qualifiedByName = "listToJson")
    MemberChildrenDO convert(AppChildrenCreateReqVO bean);

    @Mapping(source = "statusTags", target = "statusTags", qualifiedByName = "listToJson")
    @Mapping(source = "goalTags", target = "goalTags", qualifiedByName = "listToJson")
    MemberChildrenDO convert(AppChildrenUpdateReqVO bean);

    @Mapping(source = "statusTags", target = "statusTags", qualifiedByName = "jsonToList")
    @Mapping(source = "goalTags", target = "goalTags", qualifiedByName = "jsonToList")
    AppChildrenRespVO convert(MemberChildrenDO bean);

    List<AppChildrenRespVO> convertList(List<MemberChildrenDO> list);

    /**
     * 将 List 转换为 JSON 字符串
     */
    @Named("listToJson")
    default String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return JSONUtil.toJsonStr(list);
    }

    /**
     * 将 JSON 字符串转换为 List
     */
    @Named("jsonToList")
    default List<String> jsonToList(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyList();
        }
        try {
            return JSONUtil.toList(json, String.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}

