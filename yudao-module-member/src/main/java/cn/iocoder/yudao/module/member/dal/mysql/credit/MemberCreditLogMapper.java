package cn.iocoder.yudao.module.member.dal.mysql.credit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.credit.vo.CreditLogPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员信用分变动记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberCreditLogMapper extends BaseMapperX<MemberCreditLogDO> {

    default PageResult<MemberCreditLogDO> selectPage(CreditLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberCreditLogDO>()
                .eqIfPresent(MemberCreditLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MemberCreditLogDO::getChangeType, reqVO.getChangeType())
                .betweenIfPresent(MemberCreditLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberCreditLogDO::getId));
    }

    /**
     * 根据用户ID查询变动记录列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 变动记录列表
     */
    default List<MemberCreditLogDO> selectListByUserId(Long userId, Integer limit) {
        return selectList(new LambdaQueryWrapperX<MemberCreditLogDO>()
                .eq(MemberCreditLogDO::getUserId, userId)
                .orderByDesc(MemberCreditLogDO::getId)
                .last(limit != null ? "LIMIT " + limit : ""));
    }

}

