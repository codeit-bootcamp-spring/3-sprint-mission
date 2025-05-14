package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private String fileName;
    private String mimeType;
    private byte[] data;

    public BinaryContent(String fileName, String mimeType, byte[] data) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.data = data;
    }

    public static BinaryContentResponseDTO toDTO(BinaryContent binaryContent) {
        BinaryContentResponseDTO binaryContentResponseDTO = new BinaryContentResponseDTO(binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getMimeType(),
                binaryContent.getData());

        return binaryContentResponseDTO;
    }
}
