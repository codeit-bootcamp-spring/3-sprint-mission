package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);
    Optional<UserStatus> findById(String id);
    Optional<UserStatus> findByUserId(String userId);
    List<UserStatus> findAll();
    boolean existsById(String id);
    void deleteById(String id);
    void deleteByUserId(String userId);
}
