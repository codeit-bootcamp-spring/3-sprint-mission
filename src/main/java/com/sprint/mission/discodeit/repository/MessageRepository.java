package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

  /**
   * 메시지 삽입
   *
   * @param message Message
   */
  void insert(Message message);

  /**
   * 메시지 객체의 고유 아이디로 조회
   *
   * @param id UUID
   * @return Optional<Message>
   */
  Optional<Message> findById(UUID id);

  /**
   * 모든 메시지 조회
   *
   * @return List<Message>
   */
  List<Message> findAll();

  /**
   * 특정 채널의 모든 메시지를 조회
   *
   * @param channelId UUID
   * @return List<Message>
   */
  List<Message> findAllByChannelId(UUID channelId);

  /**
   * 메시지 저장 또는 업데이트
   *
   * @param message Message
   * @return Message
   */
  Message save(Message message);

  /**
   * 메시지 업데이트 (존재하지 않으면 예외)
   *
   * @param message Message
   */
  void update(Message message);

  /**
   * 메시지 객체의 고유 아이디로 삭제
   *
   * @param id UUID
   */
  void delete(UUID id);
}
