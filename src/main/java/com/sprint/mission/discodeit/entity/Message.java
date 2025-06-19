package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.ErrorMessages;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

    @Column(columnDefinition = "text", nullable = false)
    private String content;     //내용

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", columnDefinition = "uuid")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", columnDefinition = "uuid")
    private User author;        //보낸사람

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
        name = "message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();

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
