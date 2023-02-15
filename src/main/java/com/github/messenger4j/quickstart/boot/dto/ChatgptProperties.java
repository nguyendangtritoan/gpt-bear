package com.github.messenger4j.quickstart.boot.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chatgpt")
public class ChatgptProperties {

    //apiKey
    private String apiKey;

    private String model;

    private Integer maxTokens;

    private Double temperature;

    private Double topP;

    private int responseLength;

    private String apologize;

    private int maxDialog;
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public int getResponseLength() {
        return responseLength;
    }

    public void setResponseLength(int responseLength) {
        this.responseLength = responseLength;
    }

    public String getApologize() {
        return apologize;
    }

    public void setApologize(String apologize) {
        this.apologize = apologize;
    }

    public int getMaxDialog() {
        return maxDialog;
    }

    public void setMaxDialog(int maxDialog) {
        this.maxDialog = maxDialog;
    }

}