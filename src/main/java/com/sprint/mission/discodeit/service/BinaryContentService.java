package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(BinaryContentCreateRequest request, UUID userId, UUID messageId);

    Optional<BinaryContentDto> findById(UUID id);

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);
}

