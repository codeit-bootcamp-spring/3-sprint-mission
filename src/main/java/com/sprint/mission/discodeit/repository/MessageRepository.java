package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    // 메시지 생성
    Message saveMessage(Message message);

    //전체 메시지 조회
    List<Message> findAllMessage();

    // 메시지 아이디로 조회
    Optional<Message> findMessageById(UUID messageId);

    // 채널의 메시지 조회
    List<Message> findMessageByChannel(UUID channelId);

    // 유저가 보낸 메시지 조회
    List<Message> userMessage(UUID senderId);

    // 메시지 삭제
    void deletedMessage(UUID messageId);

}
