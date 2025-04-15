package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

  /**
   * 새 메시지를 생성한다
   *
   * @param content   메시지 내용
   * @param userId    작성자 ID
   * @param channelId 채널 ID
   * @return 생성된 메시지 객체
   */
  Message createMessage(String content, UUID userId, UUID channelId);

  /**
   * ID로 메시지를 조회한다
   *
   * @param id 메시지 ID
   * @return 조회된 메시지 객체
   */
  Optional<Message> getMessageById(UUID id);

  /**
   * 조건에 맞는 메시지를 검색한다
   *
   * @param channelId 채널 ID (null인 경우 모든 채널)
   * @param userId    작성자 ID (null인 경우 모든 작성자)
   * @param content   메시지 내용에 포함된 텍스트 (null인 경우 모든 내용)
   * @return 검색된 메시지 목록
   */
  List<Message> searchMessages(UUID channelId, UUID userId, String content);

  /**
   * 채널의 모든 메시지를 조회한다
   *
   * @param channelId 채널 ID
   * @return 채널 내 모든 메시지 목록
   */
  List<Message> getChannelMessages(UUID channelId);

  /**
   * 메시지 내용을 업데이트한다
   *
   * @param id      메시지 ID
   * @param content 새로운 내용
   * @return 업데이트된 메시지 객체
   */
  Optional<Message> updateMessageContent(UUID id, String content);

  /**
   * 메시지를 삭제한다
   *
   * @param id 삭제할 메시지 ID
   * @return 메시지 삭제 성공 여부
   */
  Optional<Message> deleteMessage(UUID id);
}