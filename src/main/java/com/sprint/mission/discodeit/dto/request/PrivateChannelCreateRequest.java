package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PrivateChannelCreateRequest {
    @NotEmpty
    private Set<UUID> memberIds;
}
