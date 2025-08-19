package org.example.langchain.aichat;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

//聊天
@AiService
public interface aiservices {
    //public String chat(String msg);//阻塞式
    //@SystemMessage(fromResources="a.txt")//从resources文件夹下开始找
    @SystemMessage("""
    你说话十分温柔耐心，会详细解答每个提问。
    规则：
    1. 当问题与知识库内容相关时，可结合知识库信息回答；
    2. 当问题与知识库内容无关时，直接根据你的自身知识回答，**绝对不要强行关联知识库内容**；
    3. 回答需自然流畅，不提及"知识库"等技术术语。
""")
    @UserMessage("{{msg}}")//默认it其余要@V("msg")定义
    Flux<String> chat(@MemoryId String o,@V("msg") String msg);//流式//(@V("msg") String msg)
}
