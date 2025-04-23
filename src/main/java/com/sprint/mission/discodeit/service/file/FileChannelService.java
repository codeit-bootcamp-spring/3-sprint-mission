package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.channelRepository = new FileChannelRepository();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String name, UUID userId) {
        userService.getUser(userId); // 유효성 검사
        return channelRepository.save(new Channel(name, userId));
    }

    @Override
    public Channel getChannel(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(Channel channel, String newName) {
        channel.updateName(newName);
        return channelRepository.save(channel);
    }

    @Override
    public Channel deleteChannel(Channel channel) {
        return channelRepository.delete(channel);
    }
}
