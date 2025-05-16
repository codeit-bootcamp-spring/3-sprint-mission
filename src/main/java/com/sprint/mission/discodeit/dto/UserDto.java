package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;


public record UserDto(UUID userId,
                      @Schema(description = "사용자 이름", example = "kate")
                      String name, String email, UUID profileId,
                      Boolean online) {

}
