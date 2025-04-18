package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    // 생성자에서 UserRepository 주입
    public BasicUserService(UserRepository userRepository){
        this.userRepository = userRepository;

    }

    @Override
    public User createUser(String userName,UUID channelId){
        User user = new User(userName);
        userRepository.save(user);
        return user;
    }

    @Override
    public Optional<User> readUser(UUID id){
        return Optional.ofNullable(userRepository.load().get(id));
    }

    @Override
    public Map<UUID, User> readUsers() {
        return userRepository.load();
    }

    @Override
    public User updateUser(UUID id, String userName){
        User user = userRepository.load().get(id);
        user.updateUserName(userName);
        userRepository.save(user);
        return user;
    }

    @Override
    public User deleteUser(UUID id){
        userRepository.deleteUser(id);
        User user = userRepository.load().get(id);
        userRepository.save(user);
        return user;
    }

}
