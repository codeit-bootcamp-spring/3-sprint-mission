package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageRepository {

    Message save(Message message); // 메시지 저장/업데이트

    Optional<Message> findById(UUID messageId); // 메시지 조회

    List<Message> findAll(); // 전체 메시지 목록

    List<Message> findByChannelId(UUID channelId); // 채널별 메시지 목록

    List<Message> findByAuthorId(UUID authorId); // 작성자별 메시지 목록

    List<Message> findAllByChannelIdOrderByCreatedAtAsc(UUID channelId); // 채널별 정렬된 메시지 목록

    void deleteById(UUID messageId); // 메시지 삭제
}
