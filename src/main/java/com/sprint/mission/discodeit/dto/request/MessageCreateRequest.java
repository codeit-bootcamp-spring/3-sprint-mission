package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.common.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
        @NotBlank(message = Constants.ErrorMessages.MESSAGE_CONTENT_REQUIRED) String content,

        @NotNull(message = Constants.ErrorMessages.CHANNEL_ID_REQUIRED) UUID channelId,

        @NotNull(message = Constants.ErrorMessages.AUTHOR_ID_REQUIRED) UUID authorId) {

}
