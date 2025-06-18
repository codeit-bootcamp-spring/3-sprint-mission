package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import java.util.UUID;

public record UserResponseDto(UUID id, String username,
                              String email, BinaryContentResponseDto profile, Boolean online) {

}
