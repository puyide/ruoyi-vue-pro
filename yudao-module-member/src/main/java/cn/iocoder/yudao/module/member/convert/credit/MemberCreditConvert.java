package cn.iocoder.yudao.module.member.convert.credit;

import cn.iocoder.yudao.module.member.controller.app.credit.vo.AppCreditLogRespVO;
import cn.iocoder.yudao.module.member.controller.app.credit.vo.AppCreditScoreRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditLogDO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditScoreDO;
import cn.iocoder.yudao.module.member.enums.credit.CreditChangeTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员信用分 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberCreditConvert {

    MemberCreditConvert INSTANCE = Mappers.getMapper(MemberCreditConvert.class);

    @Mapping(target = "creditLevel", source = "totalScore", qualifiedByName = "scoreToCreditLevel")
    AppCreditScoreRespVO convert(MemberCreditScoreDO bean);

    @Mapping(target = "changeTypeName", source = "changeType", qualifiedByName = "changeTypeToName")
    AppCreditLogRespVO convert(MemberCreditLogDO bean);

    List<AppCreditLogRespVO> convertList(List<MemberCreditLogDO> list);

    /**
     * 根据信用分计算信用等级
     */
    @Named("scoreToCreditLevel")
    default String scoreToCreditLevel(Integer totalScore) {
        if (totalScore == null) {
            return "未知";
        }
        if (totalScore >= 90) {
            return "优秀";
        } else if (totalScore >= 80) {
            return "良好";
        } else if (totalScore >= 60) {
            return "一般";
        } else if (totalScore >= 40) {
            return "较差";
        } else {
            return "很差";
        }
    }

    /**
     * 变动类型转换为名称
     */
    @Named("changeTypeToName")
    default String changeTypeToName(Integer changeType) {
        if (changeType == null) {
            return "未知";
        }
        CreditChangeTypeEnum typeEnum = CreditChangeTypeEnum.valueOf(changeType);
        return typeEnum != null ? typeEnum.getName() : "未知";
    }

}

