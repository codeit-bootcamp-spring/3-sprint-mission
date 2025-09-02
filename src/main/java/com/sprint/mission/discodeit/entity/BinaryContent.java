package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "binary_contents")
public class BinaryContent extends BaseUpdatableEntity {

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private BinaryContentStatus status;

  private BinaryContent(
      String fileName,
      Long size,
      String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.status = BinaryContentStatus.PROCESSING;
  }

  public static BinaryContent create(
      String fileName,
      Long size,
      String contentType) {
    return BinaryContent.builder()
        .fileName(Objects.requireNonNull(fileName))
        .size(size)
        .contentType(Objects.requireNonNull(contentType))
        .status(BinaryContentStatus.PROCESSING)
        .build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String fileName;
    private Long size;
    private String contentType;
    private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

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

    public Builder status(BinaryContentStatus status) {
      this.status = status;
      return this;
    }

    public BinaryContent build() {
      BinaryContent binaryContent = new BinaryContent(fileName, size, contentType);
      binaryContent.status = this.status;
      return binaryContent;
    }
  }

  public void assignIdForTest(UUID id) {
    this.id = id;
  }

  public void updateStatus(BinaryContentStatus status) {
    this.status = status;
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