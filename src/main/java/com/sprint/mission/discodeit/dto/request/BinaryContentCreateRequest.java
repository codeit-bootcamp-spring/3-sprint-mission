package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 이미지나 첨부 파일을 등록 생성하는 DTO")
public record BinaryContentCreateRequest(
    @Schema(description = "업로드될 파일명", example = "profile.jpg")
    String fileName,

    @Schema(description = "업로드될 파일의 MIME 타입", example = "image/jpeg")
    String contentType,

    @Schema(description = "업로드될 파일의 바이너리 데이터", type = "string", format = "byte")
    byte[] bytes
) {

}
