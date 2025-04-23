package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

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
public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID senderId;
    private UUID channelId;
    private String content;

    public Message(UUID senderId, UUID channelId, String content) {
        super();
        this.senderId = senderId;
        this.channelId = channelId;
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
        this.updatedAt = Instant.now();
    }
}
