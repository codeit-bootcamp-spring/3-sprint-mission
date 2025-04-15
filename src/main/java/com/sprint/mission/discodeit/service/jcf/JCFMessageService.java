package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService { // 왜 오류가 뜨는지도 모르겠음 ㅠㅠ 그냥 안돼 그냥 안돼 그냥 안돼
    private final Map<UUID, Message> data = new HashMap<>();;

    public JCFMessageService() {} // 문제가 해결이 안돼 ㅠㅠ

    @Override
    public void createMessage(Message message) {
        // 조건문으로 message를 받게 해야 하나? 그냥 보내면 받아야 하잖아
        this.data.put(message.getId(), message);
    }

    @Override
    public Message readMessage(UUID id) { // Q1. 왜 abstract 선언해야해? String content를 불러와야 하나?
        for (Message msg : data) {
            if (msg.getId().equals(id)) {
                return msg;
            }
        }
        return null;
    }

    @Override
    public List<Message> readMessagesByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }


    @Override
    public List<Message> readAllMessages() {
        return new ArrayList<>(data.values()); }

    }
    @Override
    public Message updateMessage(UUID existId, String newContent) { // 왜 data를 못 받지?
        Message existContent = data.get(existId); // 해당 id에 맞는 message 가져옴


        if (existContent == null) {
            return null; // 해당 id를 가진 existContent 없음
        }

        if (newContent != null && !newContent.isBlank()) {
            existContent.updateContent(newContent);
            data.put(existId, existContent); // 이게 필요해?
        }

        return data.get(existId);
    }

    @Override
    public void deleteMessage(UUID id) {
        this.data.remove(id);
    }
}
