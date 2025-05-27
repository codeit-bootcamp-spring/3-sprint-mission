package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor
@AllArgsConstructor
public class BinaryContent extends BaseEntity {

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "size")
  private Long size;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "bytes")
  private byte[] bytes;

}
