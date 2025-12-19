package cn.iocoder.yudao.module.member.dal.mysql.honor;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.honor.UserBadgeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户勋章记录 Mapper
 *
 * @author 星语家园
 */
@Mapper
public interface UserBadgeMapper extends BaseMapperX<UserBadgeDO> {

    /**
     * 查询用户是否已有某勋章
     */
    default UserBadgeDO selectByUserIdAndBadgeCode(Long userId, String badgeCode) {
        return selectOne(new LambdaQueryWrapperX<UserBadgeDO>()
                .eq(UserBadgeDO::getUserId, userId)
                .eq(UserBadgeDO::getBadgeCode, badgeCode));
    }

    /**
     * 查询用户所有勋章
     */
    default List<UserBadgeDO> selectByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<UserBadgeDO>()
                .eq(UserBadgeDO::getUserId, userId)
                .orderByAsc(UserBadgeDO::getFirstUnlockTime));
    }

    /**
     * 查询用户展示的勋章
     */
    default List<UserBadgeDO> selectDisplayedByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<UserBadgeDO>()
                .eq(UserBadgeDO::getUserId, userId)
                .eq(UserBadgeDO::getIsDisplayed, true)
                .orderByAsc(UserBadgeDO::getFirstUnlockTime));
    }

    /**
     * 统计用户勋章数量
     */
    default Long selectCountByUserId(Long userId) {
        return selectCount(UserBadgeDO::getUserId, userId);
    }
}

