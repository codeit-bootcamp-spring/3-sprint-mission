package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusUpdateRequest {
    @NotBlank
    UUID userId;

    @NotBlank
    UUID channelId;
}
