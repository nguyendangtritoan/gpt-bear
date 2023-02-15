package com.github.messenger4j.quickstart.boot.service;

import com.github.messenger4j.quickstart.boot.dto.ChatRequest;
import com.github.messenger4j.quickstart.boot.dto.ChatResponse;
import com.github.messenger4j.quickstart.boot.dto.ChatgptProperties;
import com.github.messenger4j.quickstart.boot.dto.User;
import com.github.messenger4j.quickstart.boot.exception.ChatgptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class DefaultChatgptServiceImpl implements ChatGPTService {

    ChatgptProperties chatgptProperties;

    private final String URL = "https://api.openai.com/v1/completions";

    private final String AUTHORIZATION;

    public DefaultChatgptServiceImpl(ChatgptProperties chatgptProperties) {
        this.chatgptProperties = chatgptProperties;
        AUTHORIZATION = "Bearer " + chatgptProperties.getApiKey();
    }

    @Override
    public String sendMessage(String message) {
        ChatRequest chatRequest = new ChatRequest(chatgptProperties.getModel(), message,
                chatgptProperties.getMaxTokens(), chatgptProperties.getTemperature(), chatgptProperties.getTopP());
        try {
            ChatResponse chatResponse = this.getResponse(this.buildHttpEntity(chatRequest));
            String resp = chatResponse.getChoices().get(0).getText();
            if (resp.length() > chatgptProperties.getResponseLength()) {
                return chatgptProperties.getApologize();
            }
            return chatResponse.getChoices().get(0).getText();
        } catch (Exception e) {
            log.error("Send message to GPT error: {}", e.getMessage());
            return chatgptProperties.getApologize();
        }
    }

    @Override
    public ChatResponse sendChatRequest(ChatRequest chatRequest) throws RestClientException {
        return this.getResponse(this.buildHttpEntity(chatRequest));
    }

    public HttpEntity<ChatRequest> buildHttpEntity(ChatRequest chatRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.add("Authorization", AUTHORIZATION);
        return new HttpEntity<>(chatRequest, headers);
    }

    public ChatResponse getResponse(HttpEntity<ChatRequest> chatRequestHttpEntity) throws RestClientException, ChatgptException {
        log.info("request url: {}, httpEntity: {}",URL, chatRequestHttpEntity);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ChatResponse> responseEntity = restTemplate.postForEntity(URL, chatRequestHttpEntity, ChatResponse.class);
        if (responseEntity.getStatusCode().isError()) {
            log.error("error response status: {}", responseEntity);
            throw new ChatgptException("error response status :" + responseEntity.getStatusCode().value());
        } else {
            log.info("response: {}", responseEntity);
        }
        return responseEntity.getBody();
    }

}