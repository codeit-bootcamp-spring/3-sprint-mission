package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    // create
    BinaryContentDto create(BinaryContentCreateRequest request);

    // find
    BinaryContentDto find(UUID id);

    // findAll
    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    // update
    BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status);

    // delete
    void delete(UUID id);
}