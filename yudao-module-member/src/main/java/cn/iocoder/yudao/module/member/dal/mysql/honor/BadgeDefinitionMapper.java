package cn.iocoder.yudao.module.member.dal.mysql.honor;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.honor.BadgeDefinitionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 勋章定义 Mapper
 *
 * @author 星语家园
 */
@Mapper
public interface BadgeDefinitionMapper extends BaseMapperX<BadgeDefinitionDO> {

    /**
     * 根据编码查询勋章
     */
    default BadgeDefinitionDO selectByCode(String code) {
        return selectOne(BadgeDefinitionDO::getCode, code);
    }

    /**
     * 根据触发事件查询勋章
     */
    default BadgeDefinitionDO selectByTriggerEvent(String triggerEvent) {
        return selectOne(BadgeDefinitionDO::getTriggerEvent, triggerEvent);
    }

    /**
     * 查询所有启用的勋章
     */
    default List<BadgeDefinitionDO> selectEnabledList() {
        return selectList(BadgeDefinitionDO::getStatus, 0);
    }

    /**
     * 根据类别查询勋章
     */
    default List<BadgeDefinitionDO> selectByCategory(String category) {
        return selectList(BadgeDefinitionDO::getCategory, category);
    }
}

