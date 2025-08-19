package org.example.langchain.chatfunc.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatHistoryDto {
    private String sessionId;
    private String messageType; // USER/AI/SYSTEM
    private String content;
    private LocalDateTime createdAt;
}