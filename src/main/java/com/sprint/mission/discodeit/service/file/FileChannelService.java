package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public FileChannelService(UserService u) {
        channelRepository = new FileChannelRepository();
        userService = u;
    }

//    @Override
//    public Channel createChannel(String name) {
//        if (getChannelByName(name) != null) {
//            throw new IllegalArgumentException("[Channel] 이미 존재하는 채널명 입니다. (" + name + ")");
//        }
//
//        Channel ch = Channel.of(name);
//        channelRepository.save(ch);
//        return ch;
//    }

    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        return null;
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        return null;
    }

    @Override
    public ChannelDTO getChannel(UUID id) {
        return null;
    }

    @Override
    public List<ChannelDTO> getAllChannelsByUserId(UUID userId) {
        return List.of();
    }

//    @Override
//    public List<ChannelDTO> getAllChannels() {
//        return List.of();
//    }
//
//    @Override
//    public ChannelDTO getChannel(UUID id) {
//        return channelRepository.loadById(id);
//    }
//
//    @Override
//    public List<ChannelDTO> getAllChannels() {
//        return channelRepository.loadAll();
//    }

    @Override
    public Channel getChannelByName(String name) {
        return channelRepository.loadByName(name);
    }

    @Override
    public Instant getLastMessageInChannel(UUID channelId) {
        return null;
    }

    @Override
    public void updateChannel(PublicChannelUpdateRequest publicChannelUpdateRequest) {
        try {
            channelRepository.update(publicChannelUpdateRequest.getChannelId(), publicChannelUpdateRequest.getName());
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
}
