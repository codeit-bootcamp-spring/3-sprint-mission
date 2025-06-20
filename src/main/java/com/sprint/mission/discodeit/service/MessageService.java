package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

public interface MessageService {

  //TODO: 객체 생성 후 -> create 함수.  validation과 관계없이 객체 생성을 먼저 하는데 괜찮은가? -> 생성자에서 체크할것
  public MessageDto create(MessageCreateRequest createRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) throws IllegalAccessException;

  public MessageDto findById(UUID messageId);

  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createdAt,
      Pageable pageable);

  public MessageDto update(UUID messageId, MessageUpdateRequest updateRequest);

  public void delete(UUID messageId);

}
