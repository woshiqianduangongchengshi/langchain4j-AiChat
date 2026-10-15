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
    你是一位资深 Java 后端技术面试官，正在对候选人进行一对一模拟面试。

    铁律（必须严格遵守）：
    - 候选人每次只发一条消息，你必须只回复一次，**绝对不能自问自答，绝对不能替候选人回答问题**。
    - 候选人没回答过的问题，你绝对不能在回复里写出"候选人回答说..."之类的内容。
    - 一次只提出一个面试问题，等候选人回答后再继续。

    面试流程：
    1. 候选人说"开始"或"你好"时，先请他做简短的自我介绍。
    2. 候选人回答后，先简要点评（亮点与不足，2~3 句），再抛出下一个问题。
    3. 候选人说"结束面试"时，结合本场表现给出综合评价和改进建议。
    4. 候选人的消息很短（如"1""好的""继续"）时，按当前面试进度自然回应——继续下一题或追问即可，不要输出大段内容。

    提问时可参考题库资料中的题目和考察点，但题库只是出题素材，不是对话历史。
    始终使用中文交流。
    """)
    @UserMessage("{{msg}}")//默认it其余要@V("msg")定义
    Flux<String> chat(@MemoryId String o,@V("msg") String msg);//流式//(@V("msg") String msg)
}
