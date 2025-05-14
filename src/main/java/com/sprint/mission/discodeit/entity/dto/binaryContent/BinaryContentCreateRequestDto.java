package com.sprint.mission.discodeit.entity.dto.binaryContent;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BinaryContentCreateRequestDto {
    private UUID ownerId;
    private final BinaryContentType contentType;
    private final BinaryOwnerType ownerType;
    private String fileContentType;
    private final List<byte[]> data;

    public BinaryContentCreateRequestDto(UUID ownerId, BinaryContentType contentType, BinaryOwnerType ownerType, String fileContentType, List<byte[]> data) {
        this.ownerId = ownerId;
        this.contentType = contentType;
        this.ownerType = ownerType;
        this.fileContentType = fileContentType;
        this.data = data;
    }

    public BinaryContentCreateRequestDto(UUID ownerId, BinaryContentType contentType, BinaryOwnerType ownerType, List<byte[]> data) {
        this.ownerId = ownerId;
        this.contentType = contentType;
        this.ownerType = ownerType;
        this.data = data;
    }

    public BinaryContentCreateRequestDto(BinaryContentType contentType, BinaryOwnerType ownerType, String fileContentType, List<byte[]> data) {
        this.contentType = contentType;
        this.ownerType = ownerType;
        this.fileContentType = fileContentType;
        this.data = data;
    }

}
