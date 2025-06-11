package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

    @Column
    private String content;     //내용

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;        //보낸사람

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
        name = "message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();

    public Message() {
    }

    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.content = content;
        this.channel = channel;
        this.author = author;
        this.attachments = attachments;
    }

    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
    }

    public void validateContent(List<BinaryContent> attachments) {
        boolean isContentEmpty = (content == null || content.trim().isEmpty());
        boolean hasNoAttachments = (attachments == null || attachments.isEmpty());

        if (isContentEmpty && hasNoAttachments) {
            throw new IllegalArgumentException(
                ErrorMessages.format("MessageContent", ErrorMessages.ERROR_EMPTY)
            );
        }
    }
}
