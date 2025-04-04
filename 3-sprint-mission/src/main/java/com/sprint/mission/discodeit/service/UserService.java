package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    public void create(User user);

    public List<User> readById(String userId);

    public List<User> readAll();

    public void update(String userId, String userName, String ModifiedUserId
            ,String userPassword, String userEmail);

    public void deleteById(String userId);

}
