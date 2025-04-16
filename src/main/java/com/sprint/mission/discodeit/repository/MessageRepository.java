package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID messageId);
    List<Message> findAll(); // 전체 메시지
    List<Message> findMessagesByChannelId(UUID channelId); // 채널별 메시지
    List<Message> findMessagesByUserId(UUID userId); // 사용자별 메시지
    List<Message> findMessageByContainingWord(String word); // word가 포함된 메시지
    void deleteById(UUID messageId);
}
