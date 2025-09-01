package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.handler.CreatedEvent;
import lombok.Getter;

import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.dto
 * FileName     : BinaryContentCreatedEvent
 * Author       : dounguk
 * Date         : 2025. 8. 27.
 */
@Getter
public class BinaryContentCreatedEvent extends CreatedEvent<BinaryContent> {

    private final byte[] bytes;

    public BinaryContentCreatedEvent(BinaryContent data, Instant createdAt, byte[] bytes) {
        super(data, createdAt);
        this.bytes = bytes;
    }
}
