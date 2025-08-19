package org.example.langchain.aichatmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ChatHistoryCleanupService {

    private static final Logger log = LoggerFactory.getLogger(ChatHistoryCleanupService.class);
    private final ChatMessageRepository messageRepository;

    public ChatHistoryCleanupService(ChatMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // 每天凌晨 2 点执行清理任务（cron 表达式：秒 分 时 日 月 周）
    @Scheduled (cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredChatHistory () {
        LocalDateTime now = LocalDateTime.now ();
        log.info("开始清理过期对话历史，当前时间：{}", now);

// 统计过期记录数量
        long expiredCount = messageRepository.countByExpiresAtBefore (now);
        if (expiredCount > 0) {
// 删除所有过期记录
            messageRepository.deleteByExpiresAtBefore (now);
            log.info("清理完成，共删除 {} 条过期对话记录", expiredCount);
        } else {
            log.info("没有需要清理的过期对话记录");
        }
    }
}
