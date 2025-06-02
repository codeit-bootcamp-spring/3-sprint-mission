package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto findById(UUID id);

    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids);
}
