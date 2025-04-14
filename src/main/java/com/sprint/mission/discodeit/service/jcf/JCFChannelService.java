package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    JCFUserService userService;
    private final Map<UUID, Channel> data = new HashMap<>();

    public JCFChannelService(JCFUserService userService) {
        this.userService = userService;
    }

    //----------- 채널 생성 -----------
    @Override
    public Channel createChannel(String name) {
        // 이름 중복 검사
        if (getChannelByName(name) != null) {
            System.out.println("[Channel] 채널명 중복 검사");
            System.out.println("[Channel] 이미 존재하는 채널명입니다. (" + name + ")");
            return null;
        }

        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    //----------- 단일 채널 조회 -----------
    @Override
    public Channel getChannel(UUID id) {
        return data.get(id);
    }

    //----------- 모든 채널 조회 -----------
    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    //----------- 채널명으로 채널 조회 -----------
    @Override
    public Channel getChannelByName(String name) {
        return data.values().stream()
                .filter(channel -> channel.getChannelName().equals(name))
                .findFirst()
                .orElse(null);
    }

    //----------- 채널 이름 수정 -----------
    @Override
    public void updateChannel(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.setChannelName(name);
        }
    }

    //----------- 채널 접속 -----------
    public void joinChannel(UUID userId, UUID channelId) {
        Channel channel = getChannel(channelId);
        if (channel != null) {
            channel.join(userId);
            System.out.println("[Channel] 채널에 접속했습니다.");
        }
    }

    //----------- 채널 접속 해제 -----------
    public void leaveChannel(UUID userId, UUID channelId) {
        if (!existsById(channelId)) {
            System.out.println("[Channel] 유효하지 않은 채널입니다.");
        } else if (!userService.existsById(userId)) {
            System.out.println("[Channel] 유효하지 않은 사용자입니다.");
        } else {
            getChannel(channelId).leave(userId);
        }
    }

    //----------- 채널 삭제 -----------
    @Override
    public void deleteChannel(UUID id) {
        data.remove(id);
    }

    //----------- 채널 검증 -----------
    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
