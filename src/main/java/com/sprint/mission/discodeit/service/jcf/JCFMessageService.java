package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(ChannelService channelService, UserService userService) {
        this.data = new HashMap<>();
        this.channelService = channelService;
        this.userService = userService;
    }


    @Override
    public Message createMessage(String content, UUID channelId, UUID authorId) {
        // 조건문으로 message를 받게 해야 하나? 그냥 보내면 받아야 하잖아
//        Message message = new Message(Content, channelId, authorId);
//        this.data.put(message.getId(), message);
//
//        return message;
        try {
            channelService.readChannel(channelId);
            userService.readUser(authorId);
        } catch (Exception e) {
            throw e;
        }

        Message message = new Message(content, channelId, authorId);

        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        Message messageNullable = this.data.get(id);

        return Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException(id + "ID를 가진 메시지를 찾을 수 없습니다."));
    }

//    @Override
//    public List<Message> readMessagesByChannelId(UUID channelId) {
//        return data.values().stream()
//                .filter(message -> message.getChannelId().equals(channelId))
//                .collect(Collectors.toList());
//    }


    @Override
    public List<Message> readAllMessages() {
        return this.data.values()
                .stream()
                .toList();
    }


    @Override
    public Message updateMessage(UUID id, String newContent) { // 왜 data를 못 받지?
        Message messageNullable = this.data.get(id);
        Message message = Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException(id + "ID를 가진 메시지를 찾을 수 없습니다."));
        message.updateMessage(newContent);

        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        if (!this.data.containsKey(id)) {
            throw new NoSuchElementException(id + "ID를 가진 메시지를 찾을 수 없습니다.");
        }
        this.data.remove(id);
    }
}