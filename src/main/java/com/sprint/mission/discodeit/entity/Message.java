package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

@ToString(doNotUseGetters = true, callSuper = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor /* @Builder 때문에 넣어줌 */
@Builder
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  //
  @Column(name = "content")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  // message_attachments 테이블 생성
  @BatchSize(size = 100) // N+1 문제해결
  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id"),
      uniqueConstraints = {@UniqueConstraint(columnNames = {"message_id", "attachment_id"})}
  )
  private List<BinaryContent> attachments = new ArrayList<>(); // BinaryContent

  public Message(User author, Channel channel, String content, List<BinaryContent> attachments) {
    this.author = author;
    this.channel = channel;
    this.content = content;
    this.attachments = attachments;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }
}
