package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BinaryContentService {
    // 생성
    BinaryContent create(BinaryContentCreateRequestDTO createRequestDTO);
    // 전체 조회
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    // 조회 (ID)
    BinaryContent find(UUID id);
    // 삭제
    void delete(UUID id);
}
