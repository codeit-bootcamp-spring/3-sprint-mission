package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class BinaryContent implements Serializable {

    private UUID id;
    private Instant createdAt;
    private String contentType;
    private byte[] content;

    public BinaryContent(String contentType, byte[] content) {
        this.contentType = contentType;
        this.content = content;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }


    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", contentType='" + contentType + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
