package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.common.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
        @NotBlank(message = Constants.ErrorMessages.CHANNEL_NAME_REQUIRED) @Size(max = Constants.Database.CHANNEL_NAME_MAX_LENGTH, message = Constants.ErrorMessages.CHANNEL_NAME_TOO_LONG) String name,

        @Size(max = Constants.Database.CHANNEL_DESCRIPTION_MAX_LENGTH, message = Constants.ErrorMessages.CHANNEL_DESCRIPTION_TOO_LONG) String description) {

}
