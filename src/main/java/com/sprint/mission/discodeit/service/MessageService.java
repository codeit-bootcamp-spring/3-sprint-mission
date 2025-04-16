package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface MessageService {

    public List<Message> getChannelMessages(Channel channel);

    public boolean deleteMessage(Channel channel, Message message, User user);

}
