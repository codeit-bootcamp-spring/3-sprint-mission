package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.channelRepository = new JCFChannelRepository();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String name, UUID userId) {
        userService.getUser(userId);
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

