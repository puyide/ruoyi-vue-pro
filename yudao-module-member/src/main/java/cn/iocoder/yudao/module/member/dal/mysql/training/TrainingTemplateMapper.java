package cn.iocoder.yudao.module.member.dal.mysql.training;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 训练模板 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface TrainingTemplateMapper extends BaseMapperX<TrainingTemplateDO> {

    /**
     * 根据编码查询模板
     *
     * @param code 模板编码
     * @return 训练模板
     */
    default TrainingTemplateDO selectByCode(String code) {
        return selectOne(TrainingTemplateDO::getCode, code);
    }

    /**
     * 根据训练域查询模板列表
     *
     * @param domain 训练域
     * @return 训练模板列表
     */
    default List<TrainingTemplateDO> selectListByDomain(String domain) {
        return selectList(new LambdaQueryWrapperX<TrainingTemplateDO>()
                .eqIfPresent(TrainingTemplateDO::getDomain, domain)
                .eq(TrainingTemplateDO::getIsActive, true)
                .orderByAsc(TrainingTemplateDO::getSort));
    }

    /**
     * 查询所有启用的模板
     *
     * @return 训练模板列表
     */
    default List<TrainingTemplateDO> selectActiveList() {
        return selectList(new LambdaQueryWrapperX<TrainingTemplateDO>()
                .eq(TrainingTemplateDO::getIsActive, true)
                .orderByAsc(TrainingTemplateDO::getSort));
    }

    /**
     * 分页查询模板
     *
     * @param domain 训练域（可选）
     * @param name 名称（模糊，可选）
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    default PageResult<TrainingTemplateDO> selectPage(String domain, String name, Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<TrainingTemplateDO>()
                        .eqIfPresent(TrainingTemplateDO::getDomain, domain)
                        .likeIfPresent(TrainingTemplateDO::getName, name)
                        .eq(TrainingTemplateDO::getIsActive, true)
                        .orderByAsc(TrainingTemplateDO::getSort));
    }

}

