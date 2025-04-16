package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    public Channel createChannel(String channelName, String description, User user);

    public Optional<Channel> getChannel (UUID id);

    public List<Channel> getAllChannels();

    public boolean deleteChannel (UUID id, User user);

    public boolean modifyChannelName (UUID id, User user, String name);

    public boolean modifyChannelDescription (UUID id, User user, String description);

    public boolean kickOutChannel (UUID channelId, User kickUser, User admin);

    public boolean joinChannel(Channel channel, User user);

    public boolean addMessageToChannel (UUID channelId, Message message);
}
