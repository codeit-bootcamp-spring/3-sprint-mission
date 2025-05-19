package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    // 생성
    ReadStatus create(ReadStatusCreateRequest createRequestDTO);

    // 전체 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 조회 (id)
    ReadStatus find(UUID id);

    // 수정
    ReadStatus update(UUID id, ReadStatusUpdateRequest updateRequestDTO);

    // 삭제
    void delete(UUID id);
}
