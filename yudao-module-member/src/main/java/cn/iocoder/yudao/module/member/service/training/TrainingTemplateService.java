package cn.iocoder.yudao.module.member.service.training;

import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;

import java.util.List;

/**
 * 训练模板 Service 接口
 *
 * @author 芋道源码
 */
public interface TrainingTemplateService {

    /**
     * 获得训练模板
     *
     * @param id 编号
     * @return 训练模板
     */
    TrainingTemplateDO getTemplate(Long id);

    /**
     * 根据编码获得训练模板
     *
     * @param code 编码
     * @return 训练模板
     */
    TrainingTemplateDO getTemplateByCode(String code);

    /**
     * 获得所有启用的模板列表
     *
     * @return 模板列表
     */
    List<TrainingTemplateDO> getActiveTemplateList();

    /**
     * 根据训练域获得模板列表
     *
     * @param domain 训练域
     * @return 模板列表
     */
    List<TrainingTemplateDO> getTemplateListByDomain(String domain);

    /**
     * 获取所有训练域及其模板数量
     *
     * @return 训练域统计
     */
    List<DomainStatistics> getDomainStatistics();

    /**
     * 训练域统计
     */
    record DomainStatistics(String domain, String domainName, Long count) {}

}

