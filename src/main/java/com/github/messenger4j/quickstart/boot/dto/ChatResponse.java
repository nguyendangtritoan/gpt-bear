package com.github.messenger4j.quickstart.boot.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ChatResponse {
    private String id;
    private String object;
    private LocalDate created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

}