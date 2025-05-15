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
    private String name;
    private String contentType;
    private byte[] bytes;

    public BinaryContent(String name, String contentType, byte[] bytes) {
        this.contentType = contentType;
        this.bytes = bytes;
        this.name = name;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }


    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", name='" + name + '\'' +
                ", contentType='" + contentType + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
