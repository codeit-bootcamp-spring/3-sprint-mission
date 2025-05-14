package com.sprint.mission.discodeit.service.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    public BinaryContent createBinaryContent(BinaryContentCreateRequestDto binaryContentCreateRequestDto);

    public BinaryContent findProfileImageByOwnerId(UUID ownerId);

    public List<BinaryContent> findAllAttachmentsByOwnerId(UUID ownerId);

    public boolean deleteBinaryContentById(UUID id);
}
