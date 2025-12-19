package cn.iocoder.yudao.module.member.mq.consumer.nodebb;

import cn.iocoder.yudao.module.member.api.message.user.MemberUserCreateMessage;
import cn.iocoder.yudao.module.member.service.nodebb.NodebbService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 监听会员用户创建事件，同步创建 NodeBB 用户
 *
 * @author 芋道源码
 */
@Component
@ConditionalOnProperty(prefix = "yudao.member.nodebb", name = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class NodebbUserCreateConsumer {

    @Resource
    private NodebbService nodebbService;

    @EventListener
    @Async // Spring Event 默认在 Producer 发送的线程，通过 @Async 实现异步
    public void onMessage(MemberUserCreateMessage message) {
        log.info("[NodeBB 用户同步] 收到用户创建消息：userId={}", message.getUserId());
        try {
            nodebbService.syncCreateNodeBBUser(message.getUserId());
        } catch (Exception e) {
            log.error("[NodeBB 用户同步] 同步失败：userId={}, error={}", message.getUserId(), e.getMessage(), e);
            // 不抛出异常，避免影响其他消费者
        }
    }

}

