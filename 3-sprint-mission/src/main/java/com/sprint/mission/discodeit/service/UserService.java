package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    public void create(User user);

    public List<User> readById(UUID id);

    public List<User> readAll();

    public void update(UUID id, String userName, String userId
            ,String userPassword, String userEmail);

    public void deleteById(UUID id);

}
