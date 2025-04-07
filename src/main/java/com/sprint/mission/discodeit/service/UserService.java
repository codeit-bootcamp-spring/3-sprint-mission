package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {


    public User createUser(User user);

    public User getUser(UUID id);

    public List<User> getAllUsers();

    public boolean modifyPassword(UUID id, String password, String newPassword);

    public boolean deleteUser(UUID id);

    public boolean joinChannel(Channel channel, User user);

    public boolean sendMessage(Message message, Channel channel);

    public Channel createChannel(String name, String description, User user);
}
