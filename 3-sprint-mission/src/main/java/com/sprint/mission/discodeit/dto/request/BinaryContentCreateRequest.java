package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일명은 필수 입력값입니다.")
    @Size(max = 255, message = "파일명은 255자 이하로 입력해주세요.")
    String fileName,
    @NotBlank(message = "파일 타입은 필수 입력값입니다.")
    @Size(max = 100, message = "파일 타입은 100자 이하로 입력해주세요.")
    String contentType,
    @NotBlank(message = "파일은 필수 입력값입니다.")
    byte[] bytes
) {

}