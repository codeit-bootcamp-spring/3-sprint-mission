package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(

    @NotBlank(message = "파일 명은 필수입니다.")
    @Size(min = 1, max = 255, message = "파일명은 1자 이상 255자 이하여야 합니다")
    String fileName,

    @NotBlank(message = "컨텐츠 타입명은 필수입니다.")
    @Pattern(
        regexp = "^(image|video|audio|application|text)/[a-zA-Z0-9][a-zA-Z0-9!#$&\\-\\^_]*$",
        message = "올바른 MIME 타입 형식이 아닙니다 (예: image/jpeg, application/pdf)"
    )
    String contentType,

    @NotNull(message = "파일 데이터는 필수입니다")
    @Size(min = 1, max = 10485760, message = "파일 크기는 1바이트 이상 50MB 이하여야 합니다") // 10MB = 10 * 1024 * 1024
    byte[] bytes
) {

}
