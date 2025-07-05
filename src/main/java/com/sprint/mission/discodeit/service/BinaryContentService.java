package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.vo.BinaryContentData;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface BinaryContentService {

  BinaryContentResponse create(BinaryContentData binaryContentData);

  BinaryContentResponse find(UUID binaryContentId);

  List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds);

  ResponseEntity<Resource> download(UUID binaryContentId);

  void delete(UUID id);
}
