package org.example.langchain.chatfunc;

import lombok.RequiredArgsConstructor;
import org.example.langchain.aichatmemory.ChatMessageEntity;  // 使用你的实际实体类名
import org.example.langchain.aichatmemory.ChatMessageRepository;
import org.example.langchain.chatfunc.dto.ChatHistoryDto;
import org.example.langchain.customer.User;
import org.example.langchain.customer.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatSessionService {
    // 已有的注入保持不变
    private final ChatMessageRepository messageRepository;
    private final UserChatSessionRepository sessionRepository;
    private final UserRepository userRepository;

    // 新增方法：获取会话历史
    public List<ChatHistoryDto> getSessionHistory(Authentication authentication, String sessionId) {
        // 验证用户权限
        User currentUser = (User) authentication.getPrincipal();
        sessionRepository.findByUserIdAndSessionId(currentUser.getId(), sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在或无权访问"));

        // 查询并转换为DTO
        return messageRepository.findBySessionIdOrderBySequenceNumberAsc(sessionId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 转换实体到DTO
    private ChatHistoryDto convertToDto(ChatMessageEntity entity) {
        ChatHistoryDto dto = new ChatHistoryDto();
        dto.setSessionId(entity.getSessionId());
        dto.setMessageType(entity.getMessageType());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    /**
     * 创建新会话并同步初始化消息表记录
     */
    @Transactional  // 确保会话和消息在同一事务中创建
    public UserChatSession createNewSession(Authentication authentication, String sessionName) {
        // 获取当前登录用户
        User currentUser = (User) authentication.getPrincipal();
        User dbUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 生成唯一会话ID（与原逻辑保持一致）
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        // 创建会话记录
        UserChatSession session = new UserChatSession();
        session.setUserId(dbUser.getId());
        session.setSessionId(sessionId);
        session.setSessionName(sessionName != null ? sessionName : "新会话");
        UserChatSession savedSession = sessionRepository.save(session);  // 先保存会话

        // 初始化消息表记录（会话创建时）
        ChatMessageEntity initMessage = new ChatMessageEntity();  // 使用你的实际实体类名
        initMessage.setSessionId(sessionId);  // 与会话表session_id保持一致
        initMessage.setMessageType("SYSTEM"); // 符合消息类型约束（USER/AI/SYSTEM）
        initMessage.setContent("[]");            // 空内容（无对话时）
        initMessage.setSequenceNumber(0);     // 初始序号
        // createdAt和expiresAt依赖数据库默认值（CURRENT_TIMESTAMP）
        messageRepository.save(initMessage);

        return savedSession;
    }

    /**
     * 获取用户所有会话
     */
    public List<UserChatSession> getUserSessions(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
    }

    /**
     * 删除会话（依赖数据库级联删除消息，同时保留代码双重保障）
     */
    @Transactional
    public void deleteSession(Authentication authentication, String sessionId) {
        User currentUser = (User) authentication.getPrincipal();

        // 验证会话归属
        UserChatSession session = sessionRepository.findByUserIdAndSessionId(currentUser.getId(), sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在或无权访问"));

        // 仅删除会话（依赖数据库级联删除消息）
        sessionRepository.delete(session);

        // 移除这行代码：messageRepository.deleteBySessionId(sessionId);
    }
}