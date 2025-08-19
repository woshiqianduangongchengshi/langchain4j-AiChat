package org.example.langchain.aichatmemory;


import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage; import dev.langchain4j.data.message.SystemMessage; import dev.langchain4j.data.message.UserMessage; import dev.langchain4j.store.memory.chat.ChatMemoryStore; import org.slf4j.Logger; import org.slf4j.LoggerFactory; import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {
    private static final Logger log = LoggerFactory.getLogger(PersistentChatMemoryStore.class);
    private final ChatMessageRepository messageRepository;
    // 注入 Repository
    public PersistentChatMemoryStore (ChatMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String sessionId = memoryId.toString();
        try {
            List<ChatMessageEntity> entities = messageRepository.findBySessionIdOrderBySequenceNumberAsc(sessionId);
            if (!entities.isEmpty()) {
                String json = entities.get(0).getContent();
                // 处理空内容或非法JSON
                if (json == null || json.trim().isEmpty()) {
                    return new ArrayList<>();
                }
                return messagesFromJson(json);
            }
        } catch (Exception e) {
            log.error("获取会话 [{}] 消息失败", sessionId, e);
            throw new RuntimeException("获取消息失败", e);
        }
        return new ArrayList<>();
    }
    @Override
    @Transactional
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String sessionId = memoryId.toString();
        // 核心：将整个消息列表序列化为JSON
        String messagesJson = messagesToJson(messages);

        try {
            // 先删除该会话的旧消息
            messageRepository.deleteBySessionId(sessionId);

            // 只存储一条记录，包含完整的JSON
            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setSessionId(sessionId);
            entity.setMessageType("FULL_HISTORY"); // 标记为完整历史
            entity.setContent(messagesJson); // 存储JSON字符串
            entity.setSequenceNumber(0);

            messageRepository.save(entity);
        } catch (Exception e) {
            log.error("更新会话[{}]消息失败", sessionId, e);
            throw new RuntimeException("更新消息失败", e);
        }
    }
    @Override
    public void deleteMessages(Object memoryId) {
        String sessionId = memoryId.toString();
        try {
            messageRepository.deleteBySessionId (sessionId);
        } catch (Exception e) {
            log.error ("删除会话 [{}] 消息失败", sessionId, e);
            throw new RuntimeException ("删除消息失败", e);
        }
    }
    // 辅助方法：将 ChatMessage 类型转换为数据库中的 message_type
    private String getMessageType (ChatMessage message) {
        if (message instanceof UserMessage) {
            return "USER";
        } else if (message instanceof AiMessage) {
            return "AI";
        } else if (message instanceof SystemMessage) {
            return "SYSTEM";
        } else {
            return "OTHER";
        }
    }
}


