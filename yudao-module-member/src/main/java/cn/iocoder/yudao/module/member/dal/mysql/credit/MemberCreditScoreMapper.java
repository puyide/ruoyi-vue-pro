package cn.iocoder.yudao.module.member.dal.mysql.credit;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditScoreDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员信用分 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberCreditScoreMapper extends BaseMapperX<MemberCreditScoreDO> {

    /**
     * 根据用户ID查询信用分
     *
     * @param userId 用户ID
     * @return 信用分DO
     */
    default MemberCreditScoreDO selectByUserId(Long userId) {
        return selectOne(MemberCreditScoreDO::getUserId, userId);
    }

    /**
     * 根据用户ID和分数范围查询
     *
     * @param userId 用户ID
     * @param minScore 最小分数
     * @param maxScore 最大分数
     * @return 信用分DO
     */
    default MemberCreditScoreDO selectByUserIdAndScoreRange(Long userId, Integer minScore, Integer maxScore) {
        return selectOne(new LambdaQueryWrapperX<MemberCreditScoreDO>()
                .eq(MemberCreditScoreDO::getUserId, userId)
                .ge(MemberCreditScoreDO::getTotalScore, minScore)
                .le(MemberCreditScoreDO::getTotalScore, maxScore));
    }

}

