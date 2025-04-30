package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entitiy.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    public UserStatus save(UserStatus userStatus);
    public List<UserStatus> read();
    public Optional<UserStatus> readById(UUID id);
    public Optional<UserStatus> readByUserId(UUID userId);
    public void update(UUID id, UserStatus userStatus);
    public void delete(UUID userStatusId);
}
