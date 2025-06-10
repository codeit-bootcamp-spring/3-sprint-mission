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
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity(name = "message")
@Table(name = "tbl_messages")
@Getter
@NoArgsConstructor
@DynamicUpdate
public class Message extends BaseUpdatableEntity {

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // ON DELETE CASCADE
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.SET_NULL) // ON DELETE SET NULL
    private User author;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "tbl_message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments;

    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        super.setId(UUID.randomUUID());
        super.setCreatedAt(Instant.now());
        //
        this.content = content;
        this.channel = channel;
        this.author = author;
        this.attachments = attachments;
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }
}
