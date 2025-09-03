package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Builder;

@Builder
public record BinaryContentCreatedEvent(
    UUID binaryContentId,     // DB에 저장된 BinaryContent PK
    byte[] data,              // 저장할 실제 바이너리
    String contentType,       // MIME 타입
    String originalFilename,  // 원본 파일명
    Long size                 // 바이트 크기
) {

}

