package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusCreateRequest {
    @NotBlank
    private UUID userId;

    @NotBlank
    private UUID channelId;
}
