package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자 주입
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(Message message) {
        if (userService.findById(message.getSenderId()).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (channelService.findById(message.getChannelId()).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        data.put(message.getId(), message);

        channelService.findById(message.getChannelId()).ifPresent(channel -> {
            channel.getMessageList().add(message);
            channelService.update(channel);
        });
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        Optional<Message> foundMessage = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(messageId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundMessage;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return data.values().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findMessageByContainingWord(String word) {
        return data.values().stream()
                .filter(message -> message.getContent().contains(word))
                .toList();
    }

    @Override
    public Message update(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public void deleteById(UUID messageId) {
        Message message = data.get(messageId);

        if (message != null) {
            // 메시지를 보낸 유저의 메시지 리스트에서 삭제
            userService.findById(message.getSenderId()).ifPresent(user -> {
                user.getMessages().remove(message); // 유저의 메시지 목록에서 삭제
                userService.update(user); // 변경 정보 적용
            });

            // 메시지가 속한 채널의 메시지 리스트에서 해당 메시지를 삭제
            channelService.findById(message.getChannelId()).ifPresent(channel -> {
                channel.getMessageList().remove(message); // 채녈의 메시지 목록에서 삭제
                channelService.update(channel); // 변경 정보 적용
            });
        }

        data.remove(messageId);
    }
}
