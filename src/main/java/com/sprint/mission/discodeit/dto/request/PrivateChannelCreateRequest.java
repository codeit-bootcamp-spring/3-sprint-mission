package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.common.Constants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @NotEmpty(message = Constants.ErrorMessages.PARTICIPANTS_REQUIRED) @Size(min = Constants.PrivateChannel.MIN_PARTICIPANTS, message = Constants.ErrorMessages.MIN_PARTICIPANTS_REQUIRED) List<UUID> participantIds) {

}
