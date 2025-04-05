package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : JCFChannelService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class JCFChannelService implements ChannelService {
    private final List<Channel> data;
    private MessageService messageService;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public UUID createChannel(List<User> channelUsers) {
        Channel channel = new Channel(channelUsers);
        data.add(channel);
        return channel.getId();
    }

    @Override
    public List<Channel> findChannelsById(UUID channelId) {
        return data.stream().filter(channel -> channel.getId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAllChannel() {
        return data;
    }

    @Override
    public void updateChannelName(UUID id, String title) {

        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                channel.setTitle(title);
                channel.setUpdatedAt(System.currentTimeMillis());
            }
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(channelId)) {
                messageService.deleteMessagesByChannelId(channelId);
                data.remove(i);
                break;
            }
        }
    }

    @Override
    public void addMessageInChannel(UUID channelId, Message message) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                channel.getMessages().add(message);
            }
        }
    }


    @Override
    public void deleteMessageInChannel(UUID messageId) {
        for (Channel channel : data) {
            for (int i = 0; i < channel.getMessages().size(); i++) {
                channel.getMessages().removeIf(message -> message.getId().equals(messageId));
            }
        }
    }
}

