package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    private UUID userId;
    private BinaryContentCreateRequest binaryContentCreateRequest;
}
