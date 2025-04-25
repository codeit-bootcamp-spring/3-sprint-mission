package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    public UserStatus save(UserStatus userStatus);

    public Optional<UserStatus> findById(UUID UserStatusId);

    public Optional<UserStatus> findByUserId(UUID userId);

    public List<UserStatus> findAll();

    public boolean existsById(UUID UserStatusId);

    public void deleteById(UUID UserStatusId);

}
