package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = -4189267704562128762L;
    private UUID id;
    private Instant createdAt;
    //
    private String fileName;
    private String contentType;
    private Byte[] content;
}
