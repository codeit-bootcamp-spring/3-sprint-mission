package com.sprint.mission.discodeit.Dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel
 * fileName       : ChannelCreateResponse
 * author         : doungukkim
 * date           : 2025. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 29.        doungukkim       최초 생성
 */
@Getter
public class ChannelCreateResponse {
    private final UUID id;
    private final ChannelType type;
    private final Instant updatedAt;
    private final String name;
    private final String description;

    public ChannelCreateResponse(UUID id, ChannelType type, Instant updatedAt) {
        this.id = id;
        this.type = type;
        this.updatedAt = updatedAt;
        this.name = "";
        this.description = "";
    }

    public ChannelCreateResponse(UUID id, ChannelType type, Instant updatedAt, String name, String description) {
        this.id = id;
        this.type = type;
        this.updatedAt = updatedAt;
        this.name = name;
        this.description = description;
    }
}
