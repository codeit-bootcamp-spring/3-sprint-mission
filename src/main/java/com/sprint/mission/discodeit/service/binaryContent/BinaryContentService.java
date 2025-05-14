package com.sprint.mission.discodeit.service.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    public BinaryContent createBinaryContent(BinaryContentCreateRequestDto binaryContentCreateRequestDto);

    public BinaryContent findBinaryContentById(UUID id);

    public List<BinaryContent> findAllBinaryContentById(UUID id);

    public boolean deleteBinaryContentById(UUID id);
}
