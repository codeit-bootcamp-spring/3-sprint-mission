package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentCreateRequest(byte[] byteArray, UUID userId) {
}

