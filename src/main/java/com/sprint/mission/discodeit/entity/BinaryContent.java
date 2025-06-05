package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BinaryContent implements Serializable {

  @Serial
  private static final long serialVersionUID = 8121899659000317030L;

  private final UUID id;
  private final Instant createdAt;

  private final String fileName;
  private final Long size;
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
    this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
    this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }

  public static BinaryContent createWithNow(String fileName, Long size, String contentType,
      byte[] bytes) {
    return new BinaryContent(
        UUID.randomUUID(),
        Instant.now(),
        fileName,
        size,
        contentType,
        bytes
    );
  }

  public static BinaryContent createWithTimestamp(Instant createdAt, String fileName, Long size,
      String contentType, byte[] bytes) {
    return new BinaryContent(
        UUID.randomUUID(),
        Objects.requireNonNull(createdAt),
        fileName,
        size,
        contentType,
        bytes
    );
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private UUID id;
    private Instant createdAt;

    private String fileName;
    private Long size;
    private String contentType;
    private byte[] bytes;

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder createdAt(Instant createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder fileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public Builder size(Long size) {
      this.size = size;
      return this;
    }

    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder bytes(byte[] bytes) {
      this.bytes = bytes;
      return this;
    }

    public BinaryContent build() {
      return new BinaryContent(id, createdAt, fileName, size, contentType, bytes);
    }
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