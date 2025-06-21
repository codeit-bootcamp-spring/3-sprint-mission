package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "첨부 파일 생성 요청 DTO")
public record BinaryContentCreateRequest(
        @NotBlank @Size(max = 255) String fileName,
        @NotBlank String contentType,
        @NotNull @Size(min = 1) byte[] bytes) {

}
