package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record BinaryContentCreateRequest(

    @Schema(description = "업로드된 원본 파일 이름", example = "profile.png")
    String fileName,

    @Schema(description = "파일의 MIME 타입", example = "image/png")
    String contentType,

    @Schema(description = "파일의 바이트 데이터", type = "string", format = "binary")
    byte[] bytes

) {

}
