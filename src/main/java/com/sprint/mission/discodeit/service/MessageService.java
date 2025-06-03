package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

  /**
   * 새 메시지를 생성한다
   *
   * @param command CreateMessageCommand
   * @return 생성된 메시지 객체
   */
  MessageResponse create(CreateMessageCommand command);

  /**
   * ID로 메시지를 조회한다
   *
   * @param messageId 메시지 ID
   * @return 조회된 메시지 객체
   */
  MessageResponse findById(UUID messageId);

  /**
   * 채널의 모든 메시지를 조회한다
   *
   * @param channelId 채널 ID
   * @return 채널 내 모든 메시지 목록
   */
  List<MessageResponse> findAllByChannelId(UUID channelId);

  /**
   * 채널의 모든 메시지를 커서 페이징 처리하여 조회한다
   *
   * @param channelId 채널 ID
   * @return 채널 내 모든 메시지 목록
   */
  PageResponse<MessageResponse> findAllByChannelIdWithCursor(UUID channelId, Instant cursor,
      Pageable pageable);

  /**
   * 메시지 내용을 업데이트한다
   *
   * @param messageId  메시지 ID
   * @param newContent 새로운 메시지 내용
   * @return 업데이트된 메시지 객체
   */
  MessageResponse updateContent(UUID messageId, String newContent);

  /**
   * 메시지를 삭제한다
   *
   * @param messageId 삭제할 메시지 ID
   * @return 메시지 삭제 성공 여부
   */
  void delete(UUID messageId);
}