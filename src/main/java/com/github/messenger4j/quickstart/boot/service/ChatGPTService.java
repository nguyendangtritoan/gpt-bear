package com.github.messenger4j.quickstart.boot.service;


import com.github.messenger4j.quickstart.boot.dto.ChatRequest;
import com.github.messenger4j.quickstart.boot.dto.ChatResponse;

public interface ChatGPTService {

    String sendMessage(String message) throws Exception;

    ChatResponse sendChatRequest(ChatRequest request) throws Exception;

}
