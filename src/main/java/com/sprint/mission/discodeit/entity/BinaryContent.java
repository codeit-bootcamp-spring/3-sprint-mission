package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = -6905250809722555385L;

    /* 공통 필드 */
    private UUID id;
    private Instant createdAt;

    private String fileName;    // 파일명
    private Long size;          // 파일 크기
    private String contentType; // 데이터 타입
    private byte[] content;     // byte 형태의 데이터

    @Builder
    public BinaryContent(String fileName, Long size, String contentType, byte[] content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.content = content;
    }
}
