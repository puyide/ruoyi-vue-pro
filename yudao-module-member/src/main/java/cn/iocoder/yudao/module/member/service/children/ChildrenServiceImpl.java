package cn.iocoder.yudao.module.member.service.children;

import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenUpdateReqVO;
import cn.iocoder.yudao.module.member.convert.children.ChildrenConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.children.MemberChildrenDO;
import cn.iocoder.yudao.module.member.dal.mysql.children.MemberChildrenMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.CHILDREN_NOT_EXISTS;

/**
 * 儿童个人信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ChildrenServiceImpl implements ChildrenService {

    @Resource
    private MemberChildrenMapper memberChildrenMapper;

    @Override
    public Long createChildren(Long userId, AppChildrenCreateReqVO createReqVO) {
        // 插入
        MemberChildrenDO children = ChildrenConvert.INSTANCE.convert(createReqVO);
        children.setUserId(userId);
        memberChildrenMapper.insert(children);
        // 返回
        return children.getId();
    }

    @Override
    public void updateChildren(Long userId, AppChildrenUpdateReqVO updateReqVO) {
        // 校验存在，校验是否能够操作
        validChildrenExists(userId, updateReqVO.getId());
        // 更新
        MemberChildrenDO updateObj = ChildrenConvert.INSTANCE.convert(updateReqVO);
        memberChildrenMapper.updateById(updateObj);
    }

    @Override
    public void deleteChildren(Long userId, Long id) {
        // 校验存在，校验是否能够操作
        validChildrenExists(userId, id);
        // 删除
        memberChildrenMapper.deleteById(id);
    }

    /**
     * 校验儿童信息是否存在
     */
    private void validChildrenExists(Long userId, Long id) {
        MemberChildrenDO childrenDO = getChildren(userId, id);
        if (childrenDO == null) {
            throw exception(CHILDREN_NOT_EXISTS);
        }
    }

    @Override
    public MemberChildrenDO getChildren(Long userId, Long id) {
        return memberChildrenMapper.selectByIdAndUserId(id, userId);
    }

    @Override
    public List<MemberChildrenDO> getChildrenList(Long userId) {
        return memberChildrenMapper.selectListByUserId(userId);
    }

}

