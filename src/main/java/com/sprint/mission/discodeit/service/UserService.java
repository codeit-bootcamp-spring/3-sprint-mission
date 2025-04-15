package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.UUID;

public interface UserService {
    public void create(User user);
    public void readAll();
    public void readById(UUID id);
    public void update(UUID id, User user);
    public void delete(User user);

}
