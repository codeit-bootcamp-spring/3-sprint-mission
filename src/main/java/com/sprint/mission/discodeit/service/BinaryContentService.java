package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    public BinaryContentResponse create(BinaryContentCreateRequest createRequest);

    public BinaryContentResponse find(UUID binaryContentId);

    public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds);

    public void delete(UUID binaryContentId);
}
