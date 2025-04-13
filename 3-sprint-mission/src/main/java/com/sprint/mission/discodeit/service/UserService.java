package com.sprint.mission.discodeit.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {
    void create() throws IOException;
    User read(String userid);
    List<User> readAll();
    User login() throws IOException;
    void logout(User user);
    void delete(User user);
}
