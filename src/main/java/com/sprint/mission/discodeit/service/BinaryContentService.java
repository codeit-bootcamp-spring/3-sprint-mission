package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContent create(BinaryContentCreateRequest binaryContentCreateRequest);

    BinaryContent find(UUID id);

    // id 목록으로 조회
    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);
}
