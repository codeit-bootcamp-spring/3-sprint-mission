package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "바이너리 콘텐츠 생성 요청 DTO")
public record BinaryContentCreateRequest(
        @Schema(description = "파일 이름", example = "profile.jpg")
        String fileName,

        @Schema(description = "콘텐츠 타입", example = "image/jpeg")
        String contentType,

        @Schema(description = "파일 바이트 데이터")
        byte[] bytes
) {}

