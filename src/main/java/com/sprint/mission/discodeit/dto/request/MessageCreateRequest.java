package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

@Schema(description = "Message 생성 요청 객체")
public record MessageCreateRequest(
    @NotBlank(message = "메세지 내용은 필수입니다")
    String content,
    @NotBlank(message = "사용자 ID는 필수입니다")
    @Positive(message = "사용자 ID는 양수여야합니다")
    UUID authorId,
    @NotBlank(message = "채널 ID는 필수입니다")
    @Positive(message = "채널 ID는 양수여야합니다")
    UUID channelId) {

}

