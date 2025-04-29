package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    public User save(User user);

    public Optional<User> findById(UUID userId);

    //TODO : AuthService 검증때문에 이거 필요할것같음
//    public Optional<User> findByEmail(String userEmail);

    public List<User> findAll();

    public boolean existsById(UUID id);

    public void deleteById(UUID userId);
}
