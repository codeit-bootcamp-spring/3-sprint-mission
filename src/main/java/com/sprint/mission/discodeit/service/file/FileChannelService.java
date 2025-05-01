package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public FileChannelService(UserService u) {
        channelRepository = new FileChannelRepository();
        userService = u;
    }

    @Override
    public Channel createChannel(String name) {
        if (getChannelByName(name) != null) {
            throw new IllegalArgumentException("[Channel] 이미 존재하는 채널명 입니다. (" + name + ")");
        }

        Channel ch = Channel.of(name);
        channelRepository.save(ch);
        return ch;
    }

    @Override
    public Channel getChannel(UUID id) {
        return channelRepository.loadById(id);
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.loadAll();
    }

    @Override
    public Channel getChannelByName(String name) {
        return channelRepository.loadByName(name);
    }

    @Override
    public void updateChannel(UUID id, String name) {
        try {
            channelRepository.update(id, name);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        try {
            userService.getUser(userId);
            channelRepository.join(userId, channelId);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        try {
            userService.getUser(userId);
            channelRepository.leave(userId, channelId);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteChannel(UUID id) {
        try {
            channelRepository.delete(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }
}
