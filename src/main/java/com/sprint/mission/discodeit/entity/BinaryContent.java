package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BinaryContent implements Serializable {

  @Serial
  private static final long serialVersionUID = 4098006621613024122L;
  private UUID id;
  private Instant createdAt;

  private String filename;
  private Long size;
  private String contentType;
  private byte[] data;

  public BinaryContent(String filename, Long size, String contentType, byte[] data) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.filename = filename;
    this.size = size;
    this.contentType = contentType;
    this.data = data;
  }


}
