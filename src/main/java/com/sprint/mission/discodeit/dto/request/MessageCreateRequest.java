package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(

        @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다.")
        String content,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        @NotNull(message = "수신자 아이디는 필수입니다.")
        UUID authorId
) { }
