package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users;

    public JCFUserRepository() {
        this.users = new HashMap<UUID, User>();
    }

    @Override
    public Map<UUID, User> loadFromFile() {
        return users;
    }

    @Override
    public void saveToFile(Map<UUID, User> users) {
        this.users.putAll(users);
    }
}
