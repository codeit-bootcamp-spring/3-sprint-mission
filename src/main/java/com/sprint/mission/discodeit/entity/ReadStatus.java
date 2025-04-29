package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;
@Getter
public class ReadStatus {
    UUID id;
    UUID userId;
    UUID channelId;
    Instant createdAt;
    Instant updatedAt;
}
