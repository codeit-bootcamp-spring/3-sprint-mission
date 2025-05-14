package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.*;

public interface MessageService {
    // CREATE
    void create(Message message);
    // READ
    Optional<Message> findById(UUID messageId);
    List<Message> findAll(); // 전체 메시지
    List<Message> findMessagesByChannelId(UUID channelId); // 채널별 메시지
    List<Message> findMessagesByUserId(UUID userId); // 사용자별 메시지
    List<Message> findMessageByContainingWord(String word); // word가 포함된 메시지
    // UPDATE
    Message update(Message message);
    // DELETE
    void deleteById(UUID messageId);
}
