package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

    public BinaryContent createBinaryContent(BinaryContent binaryContent);

    public BinaryContent findProfileImageByOwnerId(UUID ownerId);

    public List<BinaryContent> findAllAttachmentsByOwnerId(UUID ownerId);

    public boolean deleteBinaryContentById(UUID id);
}
