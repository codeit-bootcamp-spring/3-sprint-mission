package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "message_attachments")
@Getter
@NoArgsConstructor
public class MessageAttachment {

  @EmbeddedId
  private MessageAttachmentId id;

  @ManyToOne
  @MapsId("messageId")
  @JoinColumn(name = "message_id", nullable = false)
  private Message message;

  @ManyToOne
  @MapsId("attachmentId")
  @JoinColumn(name = "attachment_id", nullable = false)
  private BinaryContent attachment;

  public MessageAttachment(Message message, BinaryContent attachment) {
    this.message = message;
    this.attachment = attachment;
    this.id = new MessageAttachmentId(message.getId(), attachment.getId());
  }

  @Embeddable
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  @Getter
  public static class MessageAttachmentId implements Serializable {

    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "attachment_id")
    private UUID attachmentId;
  }
}