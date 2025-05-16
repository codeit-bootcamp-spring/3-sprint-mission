package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

// 수정 불가능한 객체, 따라서 수정한다는 것은 곧 삭제하고 다시 생성한다는 의미임.
@Getter
public class BinaryContent implements Serializable {

    private final UUID id;
    private final Instant createdAt;

    private String fileName;
    private String contentType;
    private byte[] data;

    public BinaryContent(String fileName, String contentType, byte[] data) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return "BinaryContent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
