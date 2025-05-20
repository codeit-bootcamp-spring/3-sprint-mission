package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

  /**
   * 새 메시지를 생성한다
   *
   * @param messageCreateRequest        MessageCreateRequest
   * @param binaryContentCreateRequests List<BinaryContentCreateRequest>
   * @return 생성된 메시지 객체
   */
  Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);

  /**
   * ID로 메시지를 조회한다
   *
   * @param id 메시지 ID
   * @return 조회된 메시지 객체
   */
  Optional<Message> findById(UUID id);

  /**
   * 채널의 모든 메시지를 조회한다
   *
   * @param channelId 채널 ID
   * @return 채널 내 모든 메시지 목록
   */
  List<Message> findAllByChannelId(UUID channelId);

  /**
   * 메시지 내용을 업데이트한다
   *
   * @param request MessageUpdateRequest
   * @return 업데이트된 메시지 객체
   */
  Optional<Message> updateContent(UUID messageId, MessageUpdateRequest request);

  /**
   * 메시지를 삭제한다
   *
   * @param id 삭제할 메시지 ID
   * @return 메시지 삭제 성공 여부
   */
  Optional<Message> delete(UUID id);
}