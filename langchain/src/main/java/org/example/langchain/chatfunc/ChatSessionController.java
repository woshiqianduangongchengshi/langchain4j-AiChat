package org.example.langchain.chatfunc;


import lombok.RequiredArgsConstructor;
import org.example.langchain.chatfunc.dto.ChatHistoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/sessions")
@RequiredArgsConstructor
public class ChatSessionController {

    private final ChatSessionService sessionService;

    /**
     * 创建新会话
     */
    @PostMapping
    public ResponseEntity<UserChatSession> createSession(
            Authentication authentication,
            @RequestBody Map<String, String> requestBody) {
        String sessionName = requestBody.get("sessionName");
        return ResponseEntity.ok(sessionService.createNewSession(authentication, sessionName));
    }

    /**
     * 获取当前用户的所有会话
     */
    @GetMapping
    public ResponseEntity<List<UserChatSession>> getUserSessions(Authentication authentication) {
        return ResponseEntity.ok(sessionService.getUserSessions(authentication));
    }

    /**
     * 删除指定会话
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            Authentication authentication,
            @PathVariable String sessionId) {
        sessionService.deleteSession(authentication, sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}/history")
    public ResponseEntity<List<ChatHistoryDto>> getSessionHistory(
            Authentication authentication,
            @PathVariable String sessionId) {
        return ResponseEntity.ok(sessionService.getSessionHistory(authentication, sessionId));
    }
}
