package org.example.langchain.aichatmemory;

import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository; import java.time.LocalDateTime; import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    // 根据会话 ID 查询消息，按序号排序
    List<ChatMessageEntity> findBySessionIdOrderBySequenceNumberAsc(String sessionId);
    // 根据会话 ID 删除所有消息
    void deleteBySessionId (String sessionId);
    // 新增：统计过期的消息数量（过期时间在当前时间之前）
    long countByExpiresAtBefore (LocalDateTime now);
    // 新增：删除所有过期的消息
    void deleteByExpiresAtBefore (LocalDateTime now);
}
