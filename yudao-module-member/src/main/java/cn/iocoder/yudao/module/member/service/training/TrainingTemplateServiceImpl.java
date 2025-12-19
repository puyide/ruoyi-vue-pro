package cn.iocoder.yudao.module.member.service.training;

import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;
import cn.iocoder.yudao.module.member.dal.mysql.training.TrainingTemplateMapper;
import cn.iocoder.yudao.module.member.enums.training.TrainingDomainEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 训练模板 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class TrainingTemplateServiceImpl implements TrainingTemplateService {

    @Resource
    private TrainingTemplateMapper trainingTemplateMapper;

    @Override
    public TrainingTemplateDO getTemplate(Long id) {
        return trainingTemplateMapper.selectById(id);
    }

    @Override
    public TrainingTemplateDO getTemplateByCode(String code) {
        return trainingTemplateMapper.selectByCode(code);
    }

    @Override
    public List<TrainingTemplateDO> getActiveTemplateList() {
        return trainingTemplateMapper.selectActiveList();
    }

    @Override
    public List<TrainingTemplateDO> getTemplateListByDomain(String domain) {
        return trainingTemplateMapper.selectListByDomain(domain);
    }

    @Override
    public List<DomainStatistics> getDomainStatistics() {
        List<TrainingTemplateDO> templates = getActiveTemplateList();
        Map<String, Long> domainCounts = templates.stream()
                .collect(Collectors.groupingBy(TrainingTemplateDO::getDomain, Collectors.counting()));
        
        List<DomainStatistics> result = new ArrayList<>();
        for (TrainingDomainEnum domain : TrainingDomainEnum.values()) {
            Long count = domainCounts.getOrDefault(domain.getCode(), 0L);
            result.add(new DomainStatistics(domain.getCode(), domain.getName(), count));
        }
        return result;
    }

}

