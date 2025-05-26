package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entitiy.BinaryContent;

import java.util.List;
import java.util.UUID;


public interface BinaryContentService {

  public BinaryContent create(BinaryContentCreateRequest request);

  public BinaryContent find(UUID binaryContentId);

  public List<BinaryContent> findAllByIdIn(List<UUID> uuidList);

  public void delete(UUID binaryContentId);
}
