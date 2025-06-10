package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "binary_contents")
@Getter
public class BinaryContent extends BaseEntity {

  @Id
  private UUID id;

  @Column(name = "file_name", nullable = false)
  private String filename;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false)
  private String contentType;

  public BinaryContent(String filename, Long size, String contentType) {
    this.id = UUID.randomUUID();
    this.filename = filename;
    this.size = size;
    this.contentType = contentType;
  }

  public void updateContent(Long newSize, String newContentType) {
    if (!Objects.equals(this.size, newSize)) {
      this.size = newSize;
    }
    if (!Objects.equals(this.contentType, newContentType)) {
      this.contentType = newContentType;
    }
  }

  protected BinaryContent() {
  }
}
