package com.github.messenger4j.quickstart.boot.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageMap implements MessageRepo {

    private static final Logger logger = LoggerFactory.getLogger(MessageMap.class);

    private final Map<String, List<String>> map = new HashMap<>();

    @Value(value = "${chatgpt.intro}")
    private String intro;

    @Value(value = "${chatgpt.max-dialog}")
    private int maxDialog;

    @Override
    public void saveMessage(String senderId, String value) {

        if(map.containsKey(senderId)) {
            logger.info("Saving old recipient: {}", senderId);
            removeIfSizeExceeded(senderId, value);
            map.get(senderId).add(value);
        }
        else {
            logger.info("Saving new recipient: {}", senderId);
            removeIfSizeExceeded(senderId, value);
            map.computeIfAbsent(senderId, k -> new ArrayList<>()).add(value);
        }
    }

    @Override
    public String getMessage(String senderId) {
        List<String> list = map.get(senderId);
        String result = "";
        if (list != null) {
            result = String.join("\n", list);
        }
        return intro + result;
    }

    private void removeIfSizeExceeded(String id, String value) {
        List<String> list = map.get(id);
        if (list != null)
            while ( (list.size() >= maxDialog)
                    || (getMessage(id).length() + value.length() >= 2000) ) {
                list.remove(0);
            }
    }
}
