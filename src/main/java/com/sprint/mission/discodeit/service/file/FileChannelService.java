package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    @Override
    public Channel createChannel(String name) {
        return null;
    }

    @Override
    public Channel getChannel(UUID id) {
        return null;
    }

    @Override
    public List<Channel> getAllChannels() {
        return List.of();
    }

    @Override
    public Channel getChannelByName(String name) {
        return null;
    }

    @Override
    public void updateChannel(UUID id, String name) {

    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {

    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {

    }

    @Override
    public void deleteChannel(UUID id) {

    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }
}
