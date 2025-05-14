package com.sprint.mission.discodeit.entity.dto.binaryContent;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class BinaryContentCreateRequestDto {
    private final UUID ownerId;
    private final BinaryContentType contentType;
    private final BinaryOwnerType ownerType;
    private final byte[] data;

    public BinaryContentCreateRequestDto(UUID ownerId, BinaryContentType contentType, BinaryOwnerType ownerType, byte[] data) {
        this.ownerId = ownerId;
        this.contentType = contentType;
        this.ownerType = ownerType;
        this.data = data;
    }
}
