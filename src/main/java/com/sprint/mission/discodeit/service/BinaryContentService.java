package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentService {

  BinaryContent create(@Valid BinaryContentCreateRequest request);

  BinaryContentDto find(@NotNull UUID binaryContentId);

  List<BinaryContentDto> findAllByIdIn(@NotNull List<UUID> binaryContentIds);

  void delete(@NotNull UUID binaryContentId);

  InputStream getRawData(@NotNull UUID binaryContentId);

  ResponseEntity<?> download(@NotNull BinaryContentDto dto);
}
