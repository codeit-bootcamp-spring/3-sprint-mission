package com.sprint.mission.discodeit.dto.BinaryContent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(

        @NotBlank(message = "파일 이름은 빈 값일 수 없습니다.")
        @Size(max = 255, message = "파일 이름은 최대 255자입니다.")
        String fileName,

        @NotBlank(message = "파일 타입은 빈 값일 수 없습니다.")
        @Size(max = 100, message = "파일 타입은 최대 100자입니다.")
        String contentType,

        @NotNull(message = "파일 내용은 비어 있을 수 없습니다.")
        byte[] bytes
) {

}