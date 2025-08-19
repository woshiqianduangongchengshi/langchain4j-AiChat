package org.example.langchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class LangchainApplication {
    public static void main(String[] args) {
        SpringApplication.run(LangchainApplication.class, args);
    }
}
