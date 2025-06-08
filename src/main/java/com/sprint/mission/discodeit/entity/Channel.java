package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@AllArgsConstructor /* @Builder 때문에 넣어줌 */
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity implements Serializable {

  private static final Long serialVersionUID = 1L;
  //
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
  //
  @Column(name = "name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ChannelType type;

  @Column(name = "description")
  private String description;

  // 채널 삭제될때 readStatus 모두 삭제
  @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<ReadStatus> readStatuses;

  // 채널 삭제될때 Message 모두 삭제
  @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Message> messages;

  /* XXX: lastMessageAt는 DB에 없음 */
  @Transient
  @Setter
  private Instant lastMessageAt;

  /* XXX: ownerId는 DB에 없음 */
  @Transient
  private UUID ownerId;

  public Channel(ChannelType type, String name, String description) {
    this.name = name;
    this.type = type;
    this.description = description;
  }


  public Channel(ChannelType type, String name, String description, UUID ownerId) {
    this.name = name;
    this.type = type;
    this.description = description;
    this.ownerId = ownerId;
  }


  public void update(String name, String description) {
    boolean anyValueUpdated = false;
    if (name != null && !name.equals(this.name)) {
      this.name = name;
      anyValueUpdated = true;
    }
    if (description != null && !description.equals(this.description)) {
      this.description = description;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault());

    String createdAtFormatted = (createdAt != null) ? formatter.format(createdAt) : null;
    String updatedAtFormatted = (updatedAt != null) ? formatter.format(updatedAt) : null;

    return "📦 Channel {\n" +
        "  id         = " + id + "\n" +
        "  createdAt  = " + createdAtFormatted + "\n" +
        "  updatedAt  = " + updatedAtFormatted + "\n" +
        "  name       = '" + name + "'\n" +
        "  type       = '" + type + "'\n" +
        "  description = '" + description + "'\n" +
        "  ownerId     = '" + ownerId + "'\n" +
        "}";
  }
}
