package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id = UUID.randomUUID();
    private long createdAt  = System.currentTimeMillis();
    private long updatedAt;
}
