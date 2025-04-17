package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
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
public class FileChannelService implements ChannelService {
    private static final String DEFAULT_CHANNEL_NAME = "'s Channel";
    private final FilePathUtil filePathUtil = new FilePathUtil();
    private final FileSerializer fileSerializer = new FileSerializer();
    private final FileChannelRepository fcr = new FileChannelRepository(filePathUtil,fileSerializer);
    private UserService userService;
    private MessageService messageService;

    public void setService(MessageService messageService, UserService userService) {
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
        Channel channel = new Channel(userId);
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
    public Channel findChannelById(UUID channelId) {
        return fcr.findChannelById(channelId);
    }


    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        List<UUID> channelIds = fileSerializer.readFile(filePathUtil.getUserFilePath(userId), User.class).getChannelIds();
        return fcr.findChannelsByChannelIds(channelIds);
    }

    @Override
    public List<Channel> findAllChannel() {
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
    public void addMessageInChannel(UUID channelId, Message message) {
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
