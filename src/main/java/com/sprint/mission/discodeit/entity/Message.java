package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {
    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", columnDefinition = "uuid")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", columnDefinition = "uuid")
    private User author;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
        name = "message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    private List<BinaryContent> attachments;

    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.content = Objects.requireNonNull(content, "Message content must not be null");
        this.channel = Objects.requireNonNull(channel, "Channel ID must not be null");
        this.author = Objects.requireNonNull(author, "Author ID must not be null");
        this.attachments = (attachments != null) ? attachments : new ArrayList<>();
    }

    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + getContent() + '\'' +
                ", user='" + getAuthor() + '\'' +
                ", channel='" + getChannel() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
