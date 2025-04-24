package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

//    public BasicChannelService(ChannelRepository chRepo) {
//        channelRepository = chRepo;
//    }

    @Override
    public Channel createChannel(String name) {
        if (getChannelByName(name) != null) {
            throw new IllegalArgumentException("[User] 이미 존재하는 사용자 이름입니다. (" + name + ")");
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
        List<Channel> channels = channelRepository.loadAll();
        return channels.stream().toList();
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
    public void deleteChannel(UUID id) {
        try {
            channelRepository.delete(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        Channel ch = getChannel(channelId);
        if (ch != null) {
            channelRepository.join(userId, channelId);
        }
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        Channel ch = getChannel(channelId);
        if (ch == null || !ch.isMember(userId)) {
            System.out.println("[Channel] 유효하지 않은 채널 혹은 사용자입니다.");
        } else {
            getChannel(channelId).leave(userId);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }
}
