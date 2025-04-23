package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {


    public User createUser(User user);

    public Optional<User> getUser(UUID id);

    public List<User> getAllUsers();

    public boolean modifyPassword(UUID id, String password, String newPassword);

    public boolean deleteUser(UUID id);


}
