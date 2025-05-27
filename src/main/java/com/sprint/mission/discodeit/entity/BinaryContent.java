package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "binary_contents", schema = "discodeit")
public class BinaryContent extends BaseEntity {

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "size")
  private Long size;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "bytes")
  private byte[] bytes;

  public BinaryContent() {
  }

  public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }

//  public static BinaryContentResponseDTO toDTO(BinaryContent binaryContent) {
//    BinaryContentResponseDTO binaryContentResponseDTO = new BinaryContentResponseDTO(
//        binaryContent.getId(),
//        binaryContent.getCreatedAt(),
//        binaryContent.getFileName(),
//        binaryContent.getContentType(),
//        binaryContent.getBytes());
//
//    return binaryContentResponseDTO;
//  }
}
