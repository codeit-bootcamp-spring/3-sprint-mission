package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(BinaryContentCreateRequest createRequest);

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    BinaryContentDto find(UUID id);

    void delete(UUID id);
}
