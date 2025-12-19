package cn.iocoder.yudao.module.member.job;

import cn.iocoder.yudao.module.member.service.peergroup.NodebbGroupSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * NodeBB 同步定时任务
 * 
 * 定时扫描 Outbox 表，处理待同步和需要重试的任务
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class NodebbSyncJob {

    @Resource
    private NodebbGroupSyncService nodebbGroupSyncService;

    /**
     * 每分钟执行一次同步任务
     * 
     * 使用 fixedDelay 确保上一次执行完成后再等待指定时间
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public void execute() {
        log.debug("[NodebbSyncJob] 开始执行 NodeBB 同步任务...");
        
        try {
            int processed = nodebbGroupSyncService.processPendingTasks(50);
            if (processed > 0) {
                log.info("[NodebbSyncJob] 完成 NodeBB 同步任务，处理数量: {}", processed);
            }
        } catch (Exception e) {
            log.error("[NodebbSyncJob] 执行同步任务异常", e);
        }
    }

}

