package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.UUID;

public interface UserRepository {
    public void save(User user);
    public void read();
    public void readById(UUID id);
    public void update(UUID id, User user);
    public void delete(User user);
}
