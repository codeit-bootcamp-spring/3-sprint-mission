package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  @Column(columnDefinition = "TEXT")
  private String content;

  @ManyToOne
  @JoinColumn(name = "channel_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Channel channel;

  @ManyToOne
  @JoinColumn(name = "author_id")
  @OnDelete(action = OnDeleteAction.SET_NULL)
  private User author;


  @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
  @JoinTable(name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id"))
  private List<BinaryContent> attachments;

  public static Message createMessage(String content, Channel channel, User author) {
    return new Message(content, channel, author, new ArrayList<>());
  }

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {

    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachments = attachments;
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void insertAttachments(BinaryContent binaryContent) {
    this.attachments.add(binaryContent);
  }

  public void deleteAttachments(BinaryContent binaryContent) {
    this.attachments.remove(binaryContent);
  }
}
