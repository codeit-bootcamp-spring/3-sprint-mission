package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;

public record UserDTO (
        Optional<User> user,
        UserStatus userStatus
) { }

