package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
  // 메시지 저장
  Message save(Message message);

  // 메시지 ID로 메시지를 조회
  Optional<Message> findById(UUID messageId);

  // 메시지를 삭제
  void delete(UUID messageId);

  // 여러 메시지를 한 번에 삭제
  void deleteAll(List<Message> messages);

  // 특정 채널에 속한 모든 메시지를 조회
  List<Message> findByChannelId(UUID channelId);

  // 특정 유저가 보낸 모든 메시지를 조회
  List<Message> findBySenderId(UUID senderId);
}
