package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 6424554869951126230L;
    private UUID id;
    private Instant cratedAt;

    private String fileName;
    private String contentType;
    private byte[] content;

    public BinaryContent(String fileName, String contentType, byte[] content) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;

        this.cratedAt = Instant.now();
        this.content = content;
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", cratedAt=" + cratedAt +
                ", content=" + Arrays.toString(content) +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
