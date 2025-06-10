package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContentResponse create(MultipartFile file);

  BinaryContentResponse findByIdOrThrow(UUID id);

  List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);
}
