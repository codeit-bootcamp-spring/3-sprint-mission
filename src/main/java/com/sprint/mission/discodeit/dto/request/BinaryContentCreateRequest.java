package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일명을 입력해주세요") String fileName,
    @NotBlank(message = "파일 타입을 입력해주세요") String contentType,
    @NotBlank(message = "파일 데이터를 입력해주세요") byte[] bytes
) {
}
