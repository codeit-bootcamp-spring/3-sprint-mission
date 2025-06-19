package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

import java.util.Optional;

public interface BinaryContentService {

  BinaryContent create(BinaryContentCreateRequest request);

  /**
   * Optional로 래핑된 BinaryContentCreateRequest 처리
   * Optional이 비어있으면 null 반환
   */
  BinaryContent createFromOptional(Optional<BinaryContentCreateRequest> optionalRequest);

  /**
   * 여러 개의 BinaryContent를 일괄 생성
   * 메시지 첨부파일 등에서 사용
   */
  List<BinaryContent> createAll(List<BinaryContentCreateRequest> requests);

  BinaryContent find(UUID binaryContentId);

  List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);

  void delete(UUID binaryContentId);
}
