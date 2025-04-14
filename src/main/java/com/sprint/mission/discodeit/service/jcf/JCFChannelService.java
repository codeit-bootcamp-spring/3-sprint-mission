package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

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
    private static final String DEFAULT_CHANNEL_NAME = "'s channel";
    private final List<Channel> data;
    private MessageService messageService;
    private UserService userService;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    public void setService(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }


    @Override
    public UUID createChannel(UUID userId) {
        // early return
        if (userService.findUserById(userId) == null) {
            return null;
        }
        Channel channel = new Channel(userId);
        String username = userService.findUserById(userId).getUsername();
        // add title
        channel.setTitle(username + DEFAULT_CHANNEL_NAME);
        // add new channel
        data.add(channel);
        // add channelId in User
        userService.addChannelInUser(userId, channel.getId());

        return channel.getId();
    }

    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        List<Channel> result = new ArrayList<>();
        List<UUID> channelIds = userService.findChannelIdsInId(userId);
        for (UUID channelId : channelIds) {
            result.add(findChannelById(channelId));
        }
        return result;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElse(null);
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
            }
        }
    }


    @Override
    public void deleteChannel(UUID channelId) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                messageService.deleteMessagesByChannelId(channelId);
                data.remove(channel);
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



    public void deleteMessageInChannel(UUID messageId) {
        for (Channel channel : data) {
            channel.getMessages().removeIf(message -> message.getId().equals(messageId));
        }
    }

    @Override
    public void addUserInChannel(UUID userId, UUID channelId) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                List<UUID> usersIds = channel.getUsersIds();
                usersIds.add(userId);
                channel.setUsersIds(usersIds);
            }
        }
    }
}

