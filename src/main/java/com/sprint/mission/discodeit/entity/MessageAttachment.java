package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "message_attachments")
public class MessageAttachment extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id", nullable = false)
  private Message message;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attachment_id", nullable = false)
  private BinaryContent binaryContent;

  private MessageAttachment(Message message, BinaryContent binaryContent) {
    this.message = Objects.requireNonNull(message, "메시지는 필수입니다.");
    this.binaryContent = Objects.requireNonNull(binaryContent, "첨부파일은 필수입니다.");
  }

  public static MessageAttachment attach(Message message, BinaryContent binaryContent) {
    return new MessageAttachment(message, binaryContent);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MessageAttachment ma)) {
      return false;
    }
    if (this.id == null || ma.id == null) {
      return false;
    }
    return this.id.equals(ma.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
