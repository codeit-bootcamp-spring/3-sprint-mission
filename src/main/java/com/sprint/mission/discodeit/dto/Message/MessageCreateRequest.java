package com.sprint.mission.discodeit.dto.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(

        @NotBlank(message = "메시지 내용은 빈 값일 수 없습니다.")
        String content,

        @NotNull(message = "사용자 ID는 필수업니다.")
        UUID authorId,

        @NotNull(message = "채널은 필수입니다.")
        UUID channelId
) {

}
