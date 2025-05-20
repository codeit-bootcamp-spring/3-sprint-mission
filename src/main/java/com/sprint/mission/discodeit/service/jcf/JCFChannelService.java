package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFChannelService implements ChannelService {

    private static volatile JCFChannelService instance;
    private final ChannelRepository channelRepository;
    private final UserService userService;

    private JCFChannelService(UserService userService, ChannelRepository channelRepository) {
        this.userService = userService;
        this.channelRepository = channelRepository;
    }

    public static JCFChannelService getInstance(UserService userService, ChannelRepository channelRepository) {
        JCFChannelService result = instance;
        if (result == null) {
            synchronized (JCFChannelService.class) {
                result = instance;
                if (result == null) {
                    result = new JCFChannelService(userService, channelRepository);
                    instance = result;
                }
            }
        }
        return result;
    }

    @Override
    public Channel createChannel(PublicChannelCreateRequest request) {
        if (userService.getUserById(request.ownerChannelId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID로는 채널을 생성할 수 없습니다.");
        }
        Channel channel = new Channel(ChannelType.PUBLIC, request.channelName(), request.password(), request.ownerChannelId());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createChannel(PrivateChannelCreateRequest request) {
        throw new UnsupportedOperationException("Private channel creation with current DTO structure is not fully implemented.");
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        Optional<Channel> channelOptional = channelRepository.findById(channelId);
        return channelOptional.orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(PublicChannelUpdateRequest request) {
        Optional<Channel> channelOptional = channelRepository.findById(request.channelId());
        Channel channel = channelOptional.orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

        if (request.channelName() != null && !request.channelName().isEmpty()) {
            channel.updateChannelName(request.channelName());
        }
        if (request.password() != null && !request.password().isEmpty()) {
            channel.updatePassword(request.password());
        }

        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.deleteById(channelId);
    }
}
