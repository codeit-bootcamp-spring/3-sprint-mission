package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    public Channel create(String channelName, String channelDescription,
                          boolean isPrivate, User creator, String password);

    public Channel readById(UUID channelId);

    public List<Channel> readAll();

    public void update(Channel channel, String ModifiedChannelName, String channelDescription,
                       boolean isPrivate, String password);

    public void deleteById(User user, Channel channel);

    public void joinChannel(User user, Channel channel, String password);

    public void leave(User user, Channel channel);

    public void addMessage(Channel channel, Message message);

    public void deleteMessage(Channel channel, Message message);

}
