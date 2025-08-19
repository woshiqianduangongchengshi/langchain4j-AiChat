package org.example.langchain.aichatmemory;

import jakarta.persistence.*; import lombok.Data;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;
    @Column(name = "message_type", nullable = false, length = 20)
    private String messageType;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    // 新增：过期时间字段（创建后 7 天过期）
    @Column (name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    // 新增：保存前自动设置过期时间为创建时间 + 7 天
    @PrePersist
    public void prePersist () {
        if (createdAt == null) {
            createdAt = LocalDateTime.now ();
        }
// 过期时间 = 创建时间 + 7 天
        expiresAt = createdAt.plusDays (7);
    }
}
