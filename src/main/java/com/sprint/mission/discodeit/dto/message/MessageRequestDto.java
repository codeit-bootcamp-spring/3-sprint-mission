package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Message 생성 및 수정 정보")
public record MessageRequestDto(
        @Size(max = 1000, message = "메시지는 1000자 이내여야 합니다.")
        String content,

        @NotNull(message = "channelId는 필수입니다.")
        UUID channelId,

        @NotNull(message = "작성자 ID는 필수입니다.")
        UUID authorId) {

}
