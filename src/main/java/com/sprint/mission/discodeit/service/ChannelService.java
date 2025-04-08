package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/**
 *packageName    : com.sprint.mission.discodeit.service
 * fileName       : ChannelService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */public interface ChannelService {


    UUID createChannel(UUID userId);

    Channel findChannelsById(UUID channelId);

    List<Channel> findChannelsByUserId(UUID userId);

    List<Channel> findAllChannel();

    void updateChannelName(UUID channelId, String title);

    void deleteChannel(UUID channelId);

    void addMessageInChannel(UUID channelId, Message message);

    void deleteMessageInChannel(UUID messageId);

    void addUserInChannel(UUID userId, UUID channelId);
}
