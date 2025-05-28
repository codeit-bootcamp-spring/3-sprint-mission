package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    public BinaryContent create(BinaryContentCreateRequest request);

    public BinaryContent find(UUID id);

    public List<BinaryContent> findAllByIdIn(List<UUID> ids);

    public void delete(UUID id);
}
