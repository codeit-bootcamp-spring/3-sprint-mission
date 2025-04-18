package com.sprint.mission.discodeit.v1.service.jcf;


import com.sprint.mission.discodeit.v1.entity.Channel1;
import com.sprint.mission.discodeit.v1.entity.Message1;
import com.sprint.mission.discodeit.v1.repository.jcf.JCFChannelRepository1;
import com.sprint.mission.discodeit.v1.service.ChannelService1;
import com.sprint.mission.discodeit.v1.service.MessageService1;
import com.sprint.mission.discodeit.v1.service.UserService1;

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
public class JCFChannelService1 implements ChannelService1 {
    private static final String DEFAULT_CHANNEL_NAME = "'s channel";
    private final JCFChannelRepository1 jcfChannelRepository = new JCFChannelRepository1();

    private MessageService1 messageService;
    private UserService1 userService;


    public void setService(MessageService1 messageService, UserService1 userService) {
        this.messageService = messageService;
        this.userService = userService;
    }


    @Override
    public UUID createChannel(UUID userId) {
        Channel1 channel = new Channel1(userId);
        // early return
        if (userService.findUserById(userId) == null) {
            return null;
        }
        String username = userService.findUserById(userId).getUsername();
        // add title
        channel.setTitle(username + DEFAULT_CHANNEL_NAME);
        // add new channel
        jcfChannelRepository.saveChannel(channel);
//        data.add(channel);
        // add channelId in User
        userService.addChannelInUser(userId, channel.getId());

        return channel.getId();
    }
    @Override
    public Channel1 findChannelById(UUID channelId) {
        return jcfChannelRepository.findChannelById(channelId);
    }

    @Override
    public List<Channel1> findChannelsByUserId(UUID userId) {
        List<UUID> channelIds = userService.findChannelIdsInId(userId);
        return jcfChannelRepository.findChannelsByChannelIds(channelIds);
    }

    @Override
    public List<Channel1> findAllChannel() {
        return jcfChannelRepository.findAllChannels();
    }

    @Override
    public void updateChannelName(UUID id, String title) {
        jcfChannelRepository.updateChannelNameById(id, title);

    }


    @Override
    public void deleteChannel(UUID channelId) {
        userService.removeChannelIdInUsers(channelId);
        jcfChannelRepository.deleteChannelById(channelId);

        messageService.deleteMessagesByChannelId(channelId);
    }


    @Override
    public void addMessageInChannel(UUID channelId, Message1 message) {
        jcfChannelRepository.addMessageInChannel(channelId, message);
    }

    @Override
    public void deleteMessageInChannel(UUID channelId, UUID messageId) {
        jcfChannelRepository.deleteMessageInChannel(channelId, messageId);
    }

    @Override
    public void addUserInChannel(UUID channelId, UUID userId) {
        jcfChannelRepository.addUserInChannel(channelId, userId);
    }
}

