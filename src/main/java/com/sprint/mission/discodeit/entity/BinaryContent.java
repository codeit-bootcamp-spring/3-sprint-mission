package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.net.URL;
import java.time.Instant;
import java.util.UUID;

public class BinaryContent {
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    //
    private final URL contentURL;

    public BinaryContent(URL contentURL) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.contentURL = contentURL;
    }
}
