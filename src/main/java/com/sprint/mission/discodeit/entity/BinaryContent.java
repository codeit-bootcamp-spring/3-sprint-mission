package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import lombok.Getter;

@Getter
public class BinaryContent extends BaseEntity {

  private String fileName;
  private String contentType;
  private byte[] bytes;

  public BinaryContent(String fileName, String contentType, byte[] bytes) {
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
