package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(List<Message> data, UserService userService, ChannelService channelService) {
        this.data = data;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(Message message,User user,Channel channel) {
        if(channel.getMembers().contains(user)){
            data.add(message);
        }
        else{
            System.out.println("Not in channel!");
        }

    }

    @Override
    public void readAll() {
        data.stream()
                .forEach(System.out::println);
    }

    @Override
    public void readById(UUID id) {
        data.stream()
                .filter((n)->n.getId().equals(id))
                .forEach(System.out::println);
    }
    @Override
    public void update(UUID id, Message message) {
        data.stream()
                .filter(mess->mess.getId().equals(id))
                .forEach(mess->{
                    mess.updateUpdatedAt(System.currentTimeMillis());
                    mess.updateEmoticon(message.getEmoticon());
                    mess.updatePicture(message.getPicture());
                    mess.updateText(message.getText());});
    }

    @Override
    public void delete(Message message) {
        data.remove(message);
    }
}
