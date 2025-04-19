package com.sprint.mission.discodeit.v1.service.file;

import com.sprint.mission.discodeit.v1.entity.Channel1;
import com.sprint.mission.discodeit.v1.entity.Message1;
import com.sprint.mission.discodeit.v1.entity.User1;
import com.sprint.mission.discodeit.v1.repository.file.FileChannelRepository1;
import com.sprint.mission.discodeit.v1.service.ChannelService1;
import com.sprint.mission.discodeit.v1.service.MessageService1;
import com.sprint.mission.discodeit.v1.service.UserService1;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;

import java.util.List;
import java.util.UUID;


/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileChannelService
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
public class FileChannelService1 implements ChannelService1 {
    private static final String DEFAULT_CHANNEL_NAME = "'s Channel";
    private final FilePathUtil filePathUtil = new FilePathUtil();
    private final FileSerializer fileSerializer = new FileSerializer();
    private final FileChannelRepository1 fcr = new FileChannelRepository1(filePathUtil,fileSerializer);
    private UserService1 userService;
    private MessageService1 messageService;

    public void setService(MessageService1 messageService, UserService1 userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

//    -------------------interface-------------------
//    UUID createChannel(UUID userId);
//    Channel findChannelById(UUID channelId);
//    List<Channel> findChannelsByUserId(UUID userId);
//    List<Channel> findAllChannel();
//    void updateChannelName(UUID channelId, String title);
//    void deleteChannel(UUID channelId);
//    void addMessageInChannel(UUID channelId, Message message); - NOT TESTED
//    void deleteMessageInChannel(UUID messageId); - NOT TESTED
//    void addUserInChannel(UUID userId, UUID channelId);
//    -------------------------------------------------

    @Override
    public UUID createChannel(UUID userId) {
        Channel1 channel = new Channel1(userId);
        // 존재하는 유저인지 확인
        if (userService.findUserById(userId) == null) {
            return null;
        }

        String username = userService.findUserById(userId).getUsername();
        channel.setTitle(username+DEFAULT_CHANNEL_NAME);

        fcr.saveChannel(channel);
        userService.addChannelInUser(userId, channel.getId());

        return channel.getId();
    }

    @Override
    public Channel1 findChannelById(UUID channelId) {
        return fcr.findChannelById(channelId);
    }


    @Override
    public List<Channel1> findChannelsByUserId(UUID userId) {
        List<UUID> channelIds = fileSerializer.readFile(filePathUtil.getUserFilePath(userId), User1.class).getChannelIds();
        return fcr.findChannelsByChannelIds(channelIds);
    }

    @Override
    public List<Channel1> findAllChannel() {
        return fcr.findAllChannels();
    }

    @Override
    public void updateChannelName(UUID channelId, String title) {
        fcr.updateChannelNameById(channelId, title);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        userService.removeChannelIdInUsers(channelId);
        fcr.deleteChannelById(channelId);
    }

    @Override
    public void addMessageInChannel(UUID channelId, Message1 message) {
        fcr.addMessageInChannel(channelId, message);

    }

    @Override
    public void deleteMessageInChannel(UUID channelId, UUID messageId) {
        fcr.deleteMessageInChannel(channelId, messageId);

    }

    @Override
    public void addUserInChannel(UUID channelId,UUID userId) {
        fcr.addUserInChannel(channelId, userId);
    }
}
