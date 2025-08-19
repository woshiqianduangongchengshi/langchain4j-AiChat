package org.example.langchain.chatfunc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserChatSessionRepository extends JpaRepository<UserChatSession, Long> {



    // 基本查询：查询所有用户，自定义查询
//    @Query("SELECT u FROM User u")
//    List<User> findAllUsers();
    // 根据用户ID查询所有会话
    List<UserChatSession> findByUserIdOrderByCreatedAtDesc(Integer userId);//jpa方法命名规范

    // 根据用户ID和会话ID查询
    Optional<UserChatSession> findByUserIdAndSessionId(Integer userId, String sessionId);

}
