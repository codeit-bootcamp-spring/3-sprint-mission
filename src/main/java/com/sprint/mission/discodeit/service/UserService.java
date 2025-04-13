package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    public User create(String userName, String userId, String userPassword, String userEmail);

    public User readById(UUID userId);

    public List<User> readAll();

    public void update(User user, String userName, String ModifiedUserId
            , String userPassword, String userEmail);

    public void deleteById(User user);

}
