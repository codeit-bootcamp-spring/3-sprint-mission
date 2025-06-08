package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@AllArgsConstructor /* @Builder 때문에 넣어줌 */
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "messages")
public class Message extends BaseUpdatableEntity implements Serializable {

  private static final long serialVersionUID = 1L;
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
  @Column(name = "content")
  private String content;
  //

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  // message_attachments 테이블 생성
  @ManyToMany
  @JoinTable(name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id"),
      uniqueConstraints = {@UniqueConstraint(columnNames = {"message_id", "attachment_id"})}
  )
  private List<BinaryContent> attachments = new ArrayList<>(); // BinaryContent

  public Message(User user, Channel channel, String content) {
    this.user = user;
    this.channel = channel;
    this.content = content;
  }

  //QUESTION. updateRequest도 안에 id를 포함할께 아니라 id, 변화될 필드 이렇게 나누는게 나을까?
  // FIXME. attachmentIds optional로 설정한거 전체적으로 수정하기
  public void update(String content, Optional<List<UUID>> attachmentIds) {
    boolean anyValueUpdated = false;
    if (content != null && !content.equals(this.content)) {
      this.content = content;
      anyValueUpdated = true;
    }

//    if (attachmentIds != null && !attachmentIds.equals(this.attachmentIds)) {
//      this.attachmentIds = attachmentIds;
//      anyValueUpdated = true;
//    }

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

    return "💬 Message {\n" +
        " id         = " + id + "\n" +
        " createdAt  = " + createdAtFormatted + "\n" +
        " updatedAt  = " + updatedAtFormatted + "\n" +
        " content       = '" + content + "'\n" +
        " user     = " + user + "\n" +
        " channel     = " + channel + "\n" +
        " attachments     = " + attachments + "\n" +
        "}";
  }
}
