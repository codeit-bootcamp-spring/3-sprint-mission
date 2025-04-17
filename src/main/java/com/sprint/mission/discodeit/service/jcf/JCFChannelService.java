package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
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
public class JCFChannelService implements ChannelService{
    private static final String DEFAULT_CHANNEL_NAME = "'s channel";
    private final JCFChannelRepository jcfChannelRepository = new JCFChannelRepository();

    private MessageService messageService;
    private UserService userService;


    public void setService(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }


    @Override
    public UUID createChannel(UUID userId) {
        Channel channel = new Channel(userId);
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
    public Channel findChannelById(UUID channelId) {
        return jcfChannelRepository.findChannelById(channelId);
    }

    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        List<UUID> channelIds = userService.findChannelIdsInId(userId);
        return jcfChannelRepository.findChannelsByChannelIds(channelIds);
    }

    @Override
    public List<Channel> findAllChannel() {
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
    public void addMessageInChannel(UUID channelId, Message message) {
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

