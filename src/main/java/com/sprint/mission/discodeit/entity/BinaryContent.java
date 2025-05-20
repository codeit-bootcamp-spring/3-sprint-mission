package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    private final UUID id;
    private final UUID ownerId; // userId 또는 messageId
    private final BinaryContentType contentType; // PROFILE_IMAGE or MESSAGE_ATTACHMENT
    private final String fileContentType;
    private final byte[] data;
    private final Instant createdAt;

    public BinaryContent(UUID ownerId, BinaryContentType contentType, String fileContentType, byte[] data) {
        this.fileContentType = fileContentType;
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.contentType = contentType;
        this.data = data;
        this.createdAt = Instant.now();
    }
}
