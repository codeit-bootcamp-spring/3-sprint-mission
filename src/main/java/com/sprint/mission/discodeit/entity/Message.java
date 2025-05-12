package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class Message implements Serializable {
    private static final long serialVersionUID = 2778505846092278216L;

    /* 공통 필드 */
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String content;// 메시지 내용
    private boolean isUpdated;          // 메시지 수정 여부

    private UUID authorId;              // 송신자 id
    private UUID channelId;             // 채널 id
    private List<UUID> attachmentIds;   // 첨부파일 리스트

    @Builder
    public Message(String content, UUID authorId, UUID channelId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachmentIds = attachmentIds;
        this.isUpdated = false;
    }

    public void update(String newContent) {
        boolean isUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            isUpdated = true;
        }

        if (isUpdated) {
            this.isUpdated = true;
            this.updatedAt = Instant.now();
        }
    }
}
