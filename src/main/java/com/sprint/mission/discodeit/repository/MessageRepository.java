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

  // 특정 채널에 속한 모든 메시지를 조회
  List<Message> findByChannelId(UUID channelId);

  // 특정 유저가 보낸 모든 메시지를 조회
  List<Message> findBySenderId(UUID senderId);

//  // 특정 채널에서 메시지를 제거
//  void removeFromChannel(UUID channelId, Message message);
//
//  // 특정 유저의 메시지 목록에서 메시지를 제거
//  void removeFromUser(UUID userId, Message message);
}
