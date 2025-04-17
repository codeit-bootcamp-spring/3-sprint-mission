package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserService userService;

    // 저장 로직과 userService의 기능 사용을 위한 생성자 주입
    public BasicChannelService(ChannelRepository channelRepository, UserService userService) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @Override
    public void save(Channel channel) {
        // 존재하지 않는 사용자를 채널 주인으로 설정하는 경우 예외 처리
        if (userService.findById(channel.getChannelMaster()).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        joinChannel(channel);

        channelRepository.save(channel);
    }
    
    @Override
    public Optional<Channel> findById(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(Channel channel) {
        // 채널 정보 변경을 해당 채널에 있는 user의 channelList에 반영
        userService.findAll().forEach(user -> {
            List<Channel> channels = user.getChannels();
            for (int i=0; i<channels.size(); i++) {
                if (channels.get(i).equals(channel)) {
                    channels.set(i, channel);
                }
            }
            userService.update(user);
        });

        return channelRepository.save(channel);
    }

    @Override
    public void deleteById(UUID channelId) {
        channelRepository.deleteById(channelId);

        // 채널에 속한 User의 channelList에서 해당 채널 삭제
        userService.findAll().forEach(user -> {
            List<Channel> channels = user.getChannels();
            for (int i=0; i<channels.size(); i++) {
                if (channels.get(i).getId().equals(channelId)) {
                    channels.remove(channels.get(i));
                }
            }
            userService.update(user);
        });
    }

    // 채널에 사용자 추가
    public void addUser(Channel channel, User user) {
        Optional<Channel> c = channelRepository.findById(channel.getId());
        Optional<User> u = userService.findById(user.getId());

        // 존재하지 않는 채널일 때 예외 처리
        if (c.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (u.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // Channel의 userList에 해당 user 추가
        if (!c.get().getUserList().contains(u.get())) {
            c.get().getUserList().add(u.get());
        }

        // User의 channelList에 해당 channel 추가
        if (!u.get().getChannels().contains(c.get())) {
            u.get().getChannels().add(c.get());
        }

        userService.update(u.get());
        channelRepository.save(c.get());
    }

    // 채널에서 사용자 제거
    public void deleteUser(Channel channel, User user) {
        Optional<Channel> c = channelRepository.findById(channel.getId());
        Optional<User> u = userService.findById(user.getId());

        // 존재하지 않는 채널일 때 예외 처리
        if (c.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (u.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // Channel의 userList에 해당 user 추가
        c.get().getUserList().remove(u.get());
        // User의 channelList에 해당 channel 추가
        u.get().getChannels().remove(c.get());

        userService.update(u.get());
        channelRepository.save(c.get());
    }

    private void joinChannel(Channel channel) {
        userService.findById(channel.getChannelMaster()).ifPresent(user -> {
            // 채널 주인은 채널 생성 시 채널에 입장
            user.getChannels().add(channel);
            channel.getUserList().add(user);
            userService.update(user);
        });
    }
}
