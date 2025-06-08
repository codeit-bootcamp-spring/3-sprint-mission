package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentDto create(BinaryContentCreateRequest request);

  Optional<BinaryContentDto> find(UUID id);

  List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);

  void update(UUID id, BinaryContentCreateRequest request);
}
