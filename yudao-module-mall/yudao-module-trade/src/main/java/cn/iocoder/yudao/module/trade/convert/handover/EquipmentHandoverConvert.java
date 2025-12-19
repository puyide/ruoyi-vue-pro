package cn.iocoder.yudao.module.trade.convert.handover;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.trade.controller.app.handover.vo.*;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentFeedbackDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverMessageDO;
import cn.iocoder.yudao.module.trade.enums.handover.ExpressStatusEnum;
import cn.iocoder.yudao.module.trade.enums.handover.HandoverModeEnum;
import cn.iocoder.yudao.module.trade.enums.handover.HandoverStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

/**
 * 器材交接 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface EquipmentHandoverConvert {

    EquipmentHandoverConvert INSTANCE = Mappers.getMapper(EquipmentHandoverConvert.class);

    @Mapping(target = "handoverStatusName", source = "handoverStatus", qualifiedByName = "statusToName")
    @Mapping(target = "handoverModeName", source = "handoverMode", qualifiedByName = "modeToName")
    @Mapping(target = "expressStatusName", source = "expressStatus", qualifiedByName = "expressStatusToName")
    @Mapping(target = "equipmentPhotos", source = "equipmentPhotos", qualifiedByName = "jsonToList")
    @Mapping(target = "returnPhotos", source = "returnPhotos", qualifiedByName = "jsonToList")
    AppHandoverRespVO convert(EquipmentHandoverDO bean);

    List<AppHandoverRespVO> convertList(List<EquipmentHandoverDO> list);

    EquipmentHandoverDO convert(AppHandoverCreateReqVO bean);

    @Mapping(target = "mediaUrls", source = "mediaUrls", qualifiedByName = "jsonToList")
    @Mapping(target = "messageTypeName", source = "messageType", qualifiedByName = "messageTypeToName")
    AppMessageRespVO convert(EquipmentHandoverMessageDO bean);

    List<AppMessageRespVO> convertMessageList(List<EquipmentHandoverMessageDO> list);

    @Mapping(target = "tags", source = "tags", qualifiedByName = "jsonToList")
    @Mapping(target = "feedbackTypeName", source = "feedbackType", qualifiedByName = "feedbackTypeToName")
    AppFeedbackRespVO convert(EquipmentFeedbackDO bean);

    List<AppFeedbackRespVO> convertFeedbackList(List<EquipmentFeedbackDO> list);

    @Named("statusToName")
    default String statusToName(Integer status) {
        if (status == null) return "未知";
        HandoverStatusEnum statusEnum = HandoverStatusEnum.valueOf(status);
        return statusEnum != null ? statusEnum.getName() : "未知";
    }

    @Named("modeToName")
    default String modeToName(Integer mode) {
        if (mode == null) return "未知";
        HandoverModeEnum modeEnum = HandoverModeEnum.valueOf(mode);
        return modeEnum != null ? modeEnum.getName() : "未知";
    }

    @Named("expressStatusToName")
    default String expressStatusToName(Integer status) {
        if (status == null) return "未知";
        ExpressStatusEnum statusEnum = ExpressStatusEnum.valueOf(status);
        return statusEnum != null ? statusEnum.getName() : "未知";
    }

    @Named("messageTypeToName")
    default String messageTypeToName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "系统消息";
            case 2: return "用户消息";
            case 3: return "图片";
            case 4: return "位置";
            case 5: return "验收消息";
            default: return "未知";
        }
    }

    @Named("feedbackTypeToName")
    default String feedbackTypeToName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "感谢";
            case 2: return "评价";
            default: return "未知";
        }
    }

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

    @Named("listToJson")
    default String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return JSONUtil.toJsonStr(list);
    }

}

