package com.sprint.mission.discodeit.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {
    User create() throws IOException;
    User find(UUID id);
    User findByUserId(String userid);
    List<User> findByName(String name);
    List<User> findAll();
    User update(UUID id) throws IOException;
    void delete(UUID id);
    // Login, Logout 메소드
    User login() throws IOException;
    void logout(User user);
}
