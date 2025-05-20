package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContent create(BinaryContentCreateRequest request);

  Optional<BinaryContent> find(UUID binaryContentid);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  void delete(UUID binaryContentId);
}
