package org.example.langchain.aichat;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.example.langchain.aichatmemory.PersistentChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.List;

@Configuration
public class chatconfig {

    @Autowired
    private PersistentChatMemoryStore persistentChatMemoryStore;
//    @Autowired
//    private OpenAiChatModel model;
    @Autowired
    private EmbeddingModel embeddingModel;

//    @Bean  //使用该方法创建存储在内存上的实例chatMemoryStore
//    public ChatMemoryStore chatMemoryStore() {
//        // InMemoryChatMemoryStore：LangChain4j提供的内存存储实现
//        return new InMemoryChatMemoryStore();
//    }


//    @Bean//这是全局单例，没有会话区分
//    public ChatMemory chatMemory(){
//        MessageWindowChatMemory memory=MessageWindowChatMemory
//                .builder()
//                .maxMessages(10)//定义最多存储10条历史会话/太多token消耗太大
//                .build();
//        return memory;
//    }
    //会话记忆，通过id区分每个对话盒子。通过memoryid区分不同会话
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){//存储对话上下文
        ChatMemoryProvider chatMemoryProvider=new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object o) {
                return MessageWindowChatMemory//可以存储对话,里面有个chatmemorystore方法存储会话，默认临时关闭程序后清空
                        .builder()
                        .maxMessages(5)//定义上下文会话多少个
                        .id(String.valueOf(o))
                        .chatMemoryStore(persistentChatMemoryStore)//如果是存储在内粗上就可以使用chatMemoryStore，为上面注释方法
                        .build();
            }
        };
        return chatMemoryProvider;
    }


    //!!!!!!下面方法属于rag，来构建本地知识库：
    //构建rag向量数据库操作对象：(langchain为你构建好了,默认为content)
    @Bean
    public EmbeddingStore Store(){
        //加载文档内存
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("knowledgefork");//一次性分割该文档所有不利于检索
        //（）中除了添加文件位置名字外逗号隔开，还能new解析文件类型方法，默认所有都能解析但pdf不如专门解析这个的，如使用pdf需要单独引入依赖
        //List<Document> documents = FileSystemDocumentLoader.loadDocuments("D:\\JAVAbcak\\langchain\\src\\main\\resources\\knowledgefork");//多级分割加快检索(需要绝对路径)
        //此外还有urlDocumentLoader根据网上的url加载(最慢，但无需下载)
        InMemoryEmbeddingStore Store = new InMemoryEmbeddingStore();
        DocumentSplitter documentSplitter= DocumentSplitters.recursive(300,100,null);//片段最大长度，前后片段重叠部分，子分割器（一般不分割再分割）
        EmbeddingStoreIngestor ingestor=EmbeddingStoreIngestor.builder()
                .embeddingStore(Store)//获取本地知识库
                .documentSplitter(documentSplitter)//使用分割器
                .embeddingModel(embeddingModel)//配置向量模型方便知识库更好的向量化
                .build();
        ingestor.ingest(documents);
        return Store;
    }

    //构建向量数据库检索对象：
    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore Store){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(Store)
                .minScore(0.7)
                .maxResults(3)
                .embeddingModel(embeddingModel)//配置向量模型方便更好的检索
                .build();
    }
}
