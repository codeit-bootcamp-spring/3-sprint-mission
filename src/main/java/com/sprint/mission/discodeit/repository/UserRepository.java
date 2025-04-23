package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.UUID;

public interface UserRepository {

    public Map<UUID, User> loadFromFile();

    public void saveToFile(Map<UUID, User> users);
}
