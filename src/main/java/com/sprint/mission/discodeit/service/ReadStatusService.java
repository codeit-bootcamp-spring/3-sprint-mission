package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {

  /**
   * 새로운 ReadStatus를 생성한다
   *
   * @param request ReadStatusCreateRequest 객체
   * @return 생성된 ReadStatus 객체
   */
  ReadStatus create(ReadStatusCreateRequest request);

  /**
   * ID로 ReadStatus를 조회한다
   *
   * @param id ReadStatus ID
   * @return Optional<ReadStatus> (존재하지 않으면 비어 있을 수 있음)
   */
  Optional<ReadStatus> find(UUID id);

  /**
   * 사용자의 모든 ReadStatus를 조회한다
   *
   * @param userId 사용자 ID
   * @return 사용자의 ReadStatus 리스트
   */
  List<ReadStatus> findAllByUserId(UUID userId);

  /**
   * ReadStatus를 업데이트한다
   *
   * @param id ReadStatus ID
   * @return 업데이트된 ReadStatus 객체
   */
  ReadStatus update(UUID id);

  /**
   * ReadStatus를 삭제한다
   *
   * @param id ReadStatus ID
   */
  void delete(UUID id);
}
