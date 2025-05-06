package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

    UserStatus save(UserStatus userStatus);
    List<UserStatus> findAll();
    UserStatus find(UUID id);
    void delete(UUID id) throws IOException;
    void deleteByUserId(UUID userId) throws IOException;
}
