package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> messages = new LinkedHashMap<>();

    // 메시지 생성
    @Override
    public Message saveMessage(Message message) {
        messages.put(message.getMessageId(), message);

        return message;
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages.values());
    }

    // 아이디로 메시지 조회
    @Override
    public Optional<Message> findMessageById(UUID messageId) {
        return Optional.of(messages.get(messageId));
    }

    // 특정 채널의 메시지 조회
    @Override
    public List<Message> findMessageByChannel(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 특정 유저의 메시지 조회
    @Override
    public List<Message> userMessage(UUID senderId) {
        return  messages.values().stream()
                .filter(message -> message.getSenderId().equals(senderId))
                .collect(Collectors.toList());
    }

    // 메시지 삭제
    @Override
    public void deletedMessage(UUID messageId) {
        messages.remove(messageId);
    }
}
