package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

//    @Override
//    public Channel createChannel(String name) {
//        // 이름 중복 검사
//        if (getChannelByName(name) != null) {
//            throw new IllegalArgumentException("\n[Channel] 이미 존재하는 채널명입니다. (" + name + ")");
//        }
//
//        Channel channel = Channel.of(name);
//        data.put(channel.getId(), channel);
//        return channel;
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

//    @Override
//    public Channel getChannel(UUID id) {
//        return data.get(id);
//    }
//
//    @Override
//    public List<Channel> getAllChannels() {
//        return new ArrayList<>(data.values());
//    }

    @Override
    public Channel getChannelByName(String name) {
        return data.values().stream()
                .filter(channel -> channel.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Instant getLastMessageInChannel(UUID channelId) {
        return null;
    }

    @Override
    public void updateChannel(PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel channel = data.get(publicChannelUpdateRequest.getChannelId());
        if (channel != null) {
            channel.setName(publicChannelUpdateRequest.getName());
        }
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {

    }

//    @Override
//    public void joinChannel(UUID userId, UUID channelId) {
//        Channel channel = getChannel(channelId);
//        if (channel != null) {
//            channel.join(userId);
//        }
//    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
//        if (!existsById(channelId)) {
//            System.out.println("[Channel] 유효하지 않은 채널입니다.");
//        } else if (!userService.existsById(userId)) {
//            System.out.println("[Channel] 유효하지 않은 사용자입니다.");
//        } else {
//            getChannel(channelId).leave(userId);
//        }
    }

    @Override
    public void deleteChannel(UUID id) {
        data.remove(id);
    }
}
