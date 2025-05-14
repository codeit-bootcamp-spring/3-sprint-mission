package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentDTO binaryContentDTO);
    BinaryContentResponseDTO findById(UUID id);
    List<BinaryContentResponseDTO> findAll();
    List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> ids);
    void deleteById(UUID id);
}
