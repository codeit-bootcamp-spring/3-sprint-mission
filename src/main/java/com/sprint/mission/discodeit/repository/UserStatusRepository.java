package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);
    List<UserStatus> findAll();
    Optional<UserStatus> findById(UUID id);
    Optional<UserStatus> findByUserId(UUID id);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    public void deleteByUserId(UUID userId);
}