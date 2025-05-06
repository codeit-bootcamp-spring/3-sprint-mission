package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface UserRepository {

    User save(User user) throws IOException;
    List<User> findAll();
    User find(UUID id);
    User findByUsername(String username);
    List<User> findByName(String name);
    User findByEmail(String email);
    boolean existsId(UUID id);
    boolean existsUsername(String username);
    boolean existsEmail(String email);
    boolean existsName(String name);
    void delete(UUID id) throws IOException;
}
