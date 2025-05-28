package com.sprint.mission.discodeit.service.command;

import com.sprint.mission.discodeit.vo.BinaryContentData;
import java.util.UUID;

public record UpdateUserCommand(
    UUID userId,
    String newName,
    String newEmail,
    String newPassword,
    BinaryContentData profile
) {

}
