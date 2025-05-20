package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  private UUID id;
  private Instant createdAt;

  private String fileName;
  private String type;
  private Long filesize;
  private byte[] bytes;

  public BinaryContent(String fileName, String type, Long filesize, byte[] bytes) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.fileName = fileName;
    this.type = type;
    this.filesize = filesize;
    this.bytes = bytes;
  }


}
