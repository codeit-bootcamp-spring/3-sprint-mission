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
  private String contentType;
  private byte[] bytes;

  public BinaryContent(String fileName, String contentType, byte[] bytes) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.fileName = fileName;
    this.contentType = contentType;
    this.bytes = bytes;
  }

  public static BinaryContentResponseDTO toDTO(BinaryContent binaryContent) {
    BinaryContentResponseDTO binaryContentResponseDTO = new BinaryContentResponseDTO(
        binaryContent.getId(),
        binaryContent.getCreatedAt(),
        binaryContent.getFileName(),
        binaryContent.getContentType(),
        binaryContent.getBytes());

    return binaryContentResponseDTO;
  }
}
