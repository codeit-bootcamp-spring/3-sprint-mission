package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private final ChannelService channelService;

    public JCFUserService(ChannelService channelService) {
        this.channelService = channelService;
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(String username, UUID channelId) {
        User user = new User(username);
        data.put(user.getId(), user);
        channelService.addUserToChannel(channelId, user.getId());
        return user;
    }

    // 다건
    @Override
    public Map<UUID, User> readUsers() {
        return data;
    }

    // 단건
    @Override
    public Optional<User> readUser(UUID id) {

        return Optional.ofNullable(data.get(id));
    }

    @Override
    public User updateUser(UUID id, String username) {
        User user = data.get(id);
        user.updateUserName(username);
        return user;
    }

    @Override
    public User deleteUser(UUID id) {

        return data.remove(id);
    }
}
