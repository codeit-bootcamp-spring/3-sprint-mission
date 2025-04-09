package com.sprint.mission.discodeit.service;

import java.util.ArrayList;
import java.util.List;
import com.sprint.mission.discodeit.entity.User;

public interface UserService {
    void create(String name);
    List<User> read(String id);
    List<User> readAll();
    User login(String id);
    void update(String id, String name);
    void delete(String id);
}
