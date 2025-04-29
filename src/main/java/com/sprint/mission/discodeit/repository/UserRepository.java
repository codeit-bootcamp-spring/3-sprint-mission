package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;


public interface UserRepository {
    User save(User user); // 저장 로직
    Optional<User> findById(UUID userId); // 저장 로직
    List<User> findAll(); // 저장 로직
    void deleteById(UUID userId); // 저장 로직
}
