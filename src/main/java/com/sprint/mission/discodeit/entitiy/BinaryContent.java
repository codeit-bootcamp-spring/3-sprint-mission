package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class BinaryContent implements Serializable {

  private UUID id;
  private Instant createdAt;
  private String fileName;
  private Long size;
  private String contentType;
  private byte[] bytes;

  public BinaryContent(String fileName, String contentType, byte[] bytes, Long size) {
    this.contentType = contentType;
    this.bytes = bytes;
    this.fileName = fileName;
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.size = size;
  }


  @Override
  public String toString() {
    return "BinaryContent{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", name='" + fileName + '\'' +
        ", size=" + size +
        ", contentType='" + contentType + '\'' +
        ", bytes=" + Arrays.toString(bytes) +
        '}';
  }
}
