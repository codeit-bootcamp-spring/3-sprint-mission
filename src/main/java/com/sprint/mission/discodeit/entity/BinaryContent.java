package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  private BinaryContent(
      String fileName,
      Long size,
      String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }

  public static BinaryContent create(
      String fileName,
      Long size,
      String contentType) {
    return BinaryContent.builder()
        .fileName(Objects.requireNonNull(fileName))
        .size(size)
        .contentType(Objects.requireNonNull(contentType))
        .build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String fileName;
    private Long size;
    private String contentType;

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

    public BinaryContent build() {
      return new BinaryContent(fileName, size, contentType);
    }
  }

  public void assignIdForTest(UUID id) {
    this.id = id;
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