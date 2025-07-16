package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 메시지의 내용이 비어있어도 첨부 파일이 있다면 전송되어야 함
 */
public record MessageCreateRequest(

    @NotNull String content,
    @NotNull UUID channelId,
    UUID authorId
) {

}