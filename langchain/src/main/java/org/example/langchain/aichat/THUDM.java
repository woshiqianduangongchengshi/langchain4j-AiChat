package org.example.langchain.aichat;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.SystemMessage;
import lombok.RequiredArgsConstructor;
import org.example.langchain.config.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class THUDM {
//    @Autowired
//    private OpenAiChatModel model;
//
//    @RequestMapping("/aichat")
//    public String chat(String message){
//        return model.chat(message);
//    }

    //使用AIservices方便rag
    private final aiservices aiservices;
    private final ContentRetriever contentRetriever;
    private static final Logger logger = LoggerFactory.getLogger(THUDM.class);//输出查看检索相关条数
    @RequestMapping(value = "/aichat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String o, String msg) {
        // 1. 检索与当前问题相关的知识库内容
        var relevantContents = contentRetriever.retrieve(Query.from(msg));

        logger.info("针对问题 [{}] 检索到相关内容 {} 条", msg, relevantContents.size());
        // 2. 构建包含相关知识的提问内容
        StringBuilder fullMessage = new StringBuilder();
        fullMessage.append("候选人的发言:").append(msg).append("\n\n");

        // 3. 只在候选人发了有实质内容的回答时才拼接题库资料
        //    像"1""好的""继续"这类短消息不传题库，避免干扰模型
        if (msg.length() >= 4 && !relevantContents.isEmpty()) {
            fullMessage.append("【仅供你参考的题库素材，不是对话历史，绝对不能把题库里的内容当成候选人的回答】:\n");
            relevantContents.forEach(content ->
                    fullMessage.append(content.textSegment()).append("\n")
            );
        }

        // 4. 调用AI服务，流式过滤掉 deepseek-r1 的 <think> 思考过程
        //    state[0] = 未处理的缓冲（处理跨 chunk 的标签）
        //    state[1] = "normal" | "inThink"
        //    state[2] = 本次新增的可见文本（emit 出去的内容）
        return aiservices.chat(o, fullMessage.toString())
                .scanWith(
                        () -> new String[]{"", "normal", ""},
                        (state, token) -> {
                            String buffer = state[0];
                            String mode = state[1];
                            buffer += token;
                            StringBuilder visible = new StringBuilder();
                            int idx;
                            while (true) {
                                if ("normal".equals(mode)) {
                                    idx = buffer.indexOf("<think>");
                                    if (idx >= 0) {
                                        visible.append(buffer, 0, idx);
                                        buffer = buffer.substring(idx + 7);
                                        mode = "inThink";
                                    } else {
                                        break;
                                    }
                                } else {
                                    idx = buffer.indexOf("</think>");
                                    if (idx >= 0) {
                                        buffer = buffer.substring(idx + 8);
                                        mode = "normal";
                                    } else {
                                        break;
                                    }
                                }
                            }
                            // 正常模式下，末尾半个 <... 标签保留在缓冲不输出
                            if ("normal".equals(mode)) {
                                int lt = buffer.lastIndexOf('<');
                                if (lt >= 0 && !buffer.substring(lt).contains(">")) {
                                    visible.append(buffer, 0, lt);
                                    buffer = buffer.substring(lt);
                                } else {
                                    visible.append(buffer);
                                    buffer = "";
                                }
                            }
                            return new String[]{buffer, mode, visible.toString()};
                        }
                )
                .map(s -> s[2])
                .filter(s -> !s.isEmpty());
    }
    //阻塞式
//    public String chat(String msg){
//        return aiservices.chat(msg);
//    }
}
