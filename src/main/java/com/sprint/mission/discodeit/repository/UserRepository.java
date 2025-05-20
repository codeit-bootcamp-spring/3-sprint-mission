package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository {
    void save(User user);
    User findById(UUID id);
    List<User> findAll();
    boolean isExistUsername(String username);
    boolean isExistEmail(String email);
    void delete(UUID userId);
}