package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@AllArgsConstructor /* @Builder 때문에 넣어줌 */
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity implements Serializable {

  private static final Long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
  //
  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  //
  @OneToOne(mappedBy = "profile")
  private User user;

  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    String createdAtFormatted = (createdAt != null) ? formatter.format(createdAt) : null;

    return "️ BinaryContent {\n" +
        "  id         = " + id + "\n" +
        "  createdAt  = " + createdAtFormatted + "\n" +
        "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BinaryContent)) {
      return false;
    }
    BinaryContent other = (BinaryContent) o;

    return (this.id == null && other.id == null)
        || (this.id != null && this.id.equals(other.id));

  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.id);
  }

}
