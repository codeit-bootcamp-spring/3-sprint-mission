package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.entity.UserStatus;

import java.util.UUID;

public interface UserStatusRepository {
    void save(UserStatus userStatus);
    UserStatus loadById(UUID id);
    void deleteById(UUID id);
}
