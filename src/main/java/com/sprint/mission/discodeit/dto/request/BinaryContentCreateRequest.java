package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "첨부 파일 생성 요청 DTO")
public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
