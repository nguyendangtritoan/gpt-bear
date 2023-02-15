package com.github.messenger4j.quickstart.boot.autoconfig;

import com.github.messenger4j.quickstart.boot.dto.ChatgptProperties;
import com.github.messenger4j.quickstart.boot.service.ChatGPTService;
import com.github.messenger4j.quickstart.boot.service.DefaultChatgptServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(ChatgptProperties.class)
public class ChatgptAutoConfiguration {

    @Autowired
    private ChatgptProperties chatgptProperties;

    public ChatgptAutoConfiguration(){
        log.debug("chatgpt-springboot-starter loaded.");
    }

    @Bean
    @ConditionalOnMissingBean(ChatGPTService.class)
    public ChatGPTService chatgptService(){
        return new DefaultChatgptServiceImpl(chatgptProperties);
    }

}
