package cn.iocoder.yudao.module.member.service.children;

import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.children.vo.AppChildrenUpdateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.children.MemberChildrenDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 儿童个人信息 Service 接口
 *
 * @author 芋道源码
 */
public interface ChildrenService {

    /**
     * 创建儿童信息
     *
     * @param userId 用户编号（监护人）
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createChildren(Long userId, @Valid AppChildrenCreateReqVO createReqVO);

    /**
     * 更新儿童信息
     *
     * @param userId 用户编号（监护人）
     * @param updateReqVO 更新信息
     */
    void updateChildren(Long userId, @Valid AppChildrenUpdateReqVO updateReqVO);

    /**
     * 删除儿童信息
     *
     * @param userId 用户编号（监护人）
     * @param id 编号
     */
    void deleteChildren(Long userId, Long id);

    /**
     * 获得儿童信息
     *
     * @param userId 用户编号（监护人）
     * @param id 编号
     * @return 儿童信息
     */
    MemberChildrenDO getChildren(Long userId, Long id);

    /**
     * 获得儿童信息列表
     *
     * @param userId 用户编号（监护人）
     * @return 儿童信息列表
     */
    List<MemberChildrenDO> getChildrenList(Long userId);

}

