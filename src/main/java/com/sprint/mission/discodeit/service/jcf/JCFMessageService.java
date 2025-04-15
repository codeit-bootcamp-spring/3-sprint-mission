package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data; //database
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(User msgUser, Channel channel, String text) {
        boolean isValidUser = this.channelService.getAttendees(channel).stream().anyMatch(user -> user.getId() == msgUser.getId());
        if (isValidUser) {
            Message msg = new Message(text, msgUser, channel);
            this.data.put(msg.getId(), msg);
        } else {
            System.out.println("Invalid user(" + msgUser.getName() + ") on this channel(" + channel.getName() + ")");
        }

    }

    @Override
    public Message read(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Message update(UUID id, String text) {
        Message selected = this.data.get(id);
        selected.update(text);
        return selected;
    }

    @Override
    public boolean delete(UUID id) {
        this.data.remove(id);

        //TODO : update return value
        return true;
    }
}
