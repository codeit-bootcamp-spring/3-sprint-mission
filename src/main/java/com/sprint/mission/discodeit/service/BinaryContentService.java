package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  public BinaryContentDto create(BinaryContentCreateRequest createRequest);

  public BinaryContentDto find(UUID binaryContentId);

  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

  public void delete(UUID binaryContentId);
}
