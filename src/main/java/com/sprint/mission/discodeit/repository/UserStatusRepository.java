package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    public UserStatus createUserStatus(UserStatus status);

    public Optional<UserStatus> findStatusById(UUID statusId);

    public Optional<UserStatus> findByUserId(UUID userId);

    public List<UserStatus> findAllStatus();

    public boolean updateUserStatus(UserStatus userStatus);

    public boolean updateByUserId(UserStatus userStatus);

    public boolean deleteUserStatus(UUID statusId);
}
