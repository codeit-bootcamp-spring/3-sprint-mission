package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotNull(message = "내용이 null값 일 수는 없습니다")
    String content,

    @NotNull(message = "채널 ID는 필수입니다")
    UUID channelId,

    @NotNull(message = "작성자 ID는 필수입니다")
    UUID authorId
) {

}
