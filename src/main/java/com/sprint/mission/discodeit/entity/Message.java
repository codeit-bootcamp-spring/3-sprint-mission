package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.entity
 * fileName       : Message2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "messages", schema = "discodeit")
public class Message extends BaseUpdatableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;



    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments;

    @Column(name = "content")
    private String content;

    // 이미지 없음
    public Message(User author, Channel channel, String content) {
        super();
        this.author = author;
        this.channel = channel;
        this.content = content;
    }

    // 이미지 있음
    public Message(User author, Channel channel, String content, List<BinaryContent> attachments) {
        super();
        this.author = author;
        this.channel = channel;
        this.content = content;
        this.attachments = attachments;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }
}
