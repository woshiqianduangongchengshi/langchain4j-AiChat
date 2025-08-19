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
        fullMessage.append("回答我的问题:").append(msg).append("\n\n");

        // 3. 只在有相关内容时才添加知识库信息
        if (!relevantContents.isEmpty()) {
            fullMessage.append("Answer using the following information:\n");
            relevantContents.forEach(content ->
                    fullMessage.append(content.textSegment()).append("\n")
            );
        }

        // 4. 调用AI服务
        return aiservices.chat(o, fullMessage.toString());
    }
    //阻塞式
//    public String chat(String msg){
//        return aiservices.chat(msg);
//    }
}
