package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  //XXX: 다른 곳에서 많이 쓰고있어서 우선 entity로 놔둠
  public BinaryContent create(BinaryContentCreateRequest createRequest);

  public BinaryContentDto find(UUID binaryContentId);

  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

  public void delete(UUID binaryContentId);
}
