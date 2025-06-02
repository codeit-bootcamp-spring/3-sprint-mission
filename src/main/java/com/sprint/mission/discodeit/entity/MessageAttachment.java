package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_attachments")
@Getter
@NoArgsConstructor
public class MessageAttachment {

  @Id
  @ManyToOne
  @JoinColumn(name = "message_id", nullable = false)
  private Message message;

  @Id
  @ManyToOne
  @JoinColumn(name = "attachment_id", nullable = false)
  private BinaryContent attachment;

  public MessageAttachment(Message message, BinaryContent attachment) {
    this.message = message;
    this.attachment = attachment;
  }
}