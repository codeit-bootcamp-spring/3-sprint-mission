package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public class BinaryContent implements Serializable {

  @Serial
  private static final long serialVersionUID = 8121899659000317030L;

  public enum ContentType {
    PROFILE_IMAGE,
    MESSAGE_ATTACHMENT
  }

  private final UUID id;
  private final Instant createdAt;

  private final String fileName;
  private Long size;
  private final String contentType;
  private final byte[] bytes;

  private BinaryContent(
      UUID id,
      Instant createdAt,
      String fileName,
      Long size,
      String contentType,
      byte[] bytes
  ) {
    this.id = id != null ? id : UUID.randomUUID();
    this.createdAt = createdAt != null ? createdAt : Instant.now();
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }

  public static BinaryContent create(
      String fileName,
      Long size,
      String contentType,
      byte[] bytes
  ) {
    return BinaryContent.builder()
        .id(UUID.randomUUID())
        .createdAt(Instant.now())
        .fileName(Objects.requireNonNull(fileName))
        .size(size)
        .contentType(Objects.requireNonNull(contentType))
        .bytes(Objects.requireNonNull(bytes))
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BinaryContent binaryContent)) {
      return false;
    }
    return Objects.equals(id, binaryContent.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}