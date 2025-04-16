package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users;
    private final ChannelService channelService;

    public JCFUserService(ChannelService channelService) {
        this.users = new HashMap<>();
        this.channelService = channelService;
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean modifyPassword(UUID id, String password, String newPassword) {
        User user = users.get(id);
        if (user == null) {
            System.out.println("존재하지 않는 회원입니다.");
            return false;
        }

        if (user.getPassword().equals(password)) {
            user.setPassword(newPassword);
            return true;
        }else {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false;
        }
    }

    @Override
    public boolean deleteUser(UUID id) {
        if (!users.containsKey(id)) {
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
        System.out.println("성공적으로 삭제되었습니다.");
        return true;
    }

}
