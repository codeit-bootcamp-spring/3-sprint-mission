package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
import java.util.Objects;

@Getter
@Entity
@Builder
@AllArgsConstructor
@DynamicUpdate
@Table(name = "messages", schema = "discodeit", indexes = {
        @Index(name = "idx_message_channel_id", columnList = "channel_id"),
        @Index(name = "idx_message_channel_created_at", columnList = "channel_id, created_at DESC")
})
public class Message extends BaseUpdatableEntity {

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    @JoinTable(name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    private List<BinaryContent> attachments;

    public Message() {
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateAttachments(List<BinaryContent> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(getId(), message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
