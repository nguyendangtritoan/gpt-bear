package com.github.messenger4j.quickstart.boot.repo;

public interface MessageRepo {
    void saveMessage(String senderId, String value);
    String getMessage(String id);
}
