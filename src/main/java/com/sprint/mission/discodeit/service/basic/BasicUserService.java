package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelService channelService;

    public BasicUserService(UserRepository userRepository, ChannelService channelService) {
        this.userRepository = userRepository;
        this.channelService = channelService;
    }

    @Override
    public User createUser(User user) {
        Map<UUID, User> users = userRepository.loadFromFile();
        users.put(user.getId(), user);
        userRepository.saveToFile(users);
        return user;
    }

    @Override
    public Optional<User> getUser(UUID id) {
        return Optional.ofNullable(userRepository.loadFromFile().get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.loadFromFile().values());
    }

    @Override
    public boolean modifyPassword(UUID id, String password, String newPassword) {
        Map<UUID, User> users = userRepository.loadFromFile();
        User user = users.get(id);

        if (user != null && user.getPassword().equals(password)) {
            user.setPassword(newPassword);
            userRepository.saveToFile(users);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUser(UUID id) {
        Map<UUID, User> users = userRepository.loadFromFile();
        if (users.containsKey(id)) {
            System.out.println("존재하지 않는 회원입니다.");
            return false;
        }

        User user = users.get(id);

        // 유저가 속한 채널 먼저 삭제
        if (!user.getChannels().isEmpty()) {
            for (Channel channel : new HashSet<>(user.getChannels())) {
                channelService.deleteChannel(channel.getId(), user);
            }
        }

        // 모든 채널에서 유저 강퇴 처리
        user.getChannels().forEach(channel -> {
            channel.removeMember(user);
        });

        users.remove(id);
        userRepository.saveToFile(users);
        System.out.println("성공적으로 삭제되었습니다.");
        return true;
    }
}
