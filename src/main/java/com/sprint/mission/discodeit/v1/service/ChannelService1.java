package com.sprint.mission.discodeit.v1.service;

import com.sprint.mission.discodeit.v1.entity.Channel1;
import com.sprint.mission.discodeit.v1.entity.Message1;

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
 */public interface ChannelService1 {


    UUID createChannel(UUID userId);

    Channel1 findChannelById(UUID channelId);

    List<Channel1> findChannelsByUserId(UUID userId);

    List<Channel1> findAllChannel();

    void updateChannelName(UUID channelId, String title);

    void deleteChannel(UUID channelId);

    void addMessageInChannel(UUID channelId, Message1 message);

    void deleteMessageInChannel(UUID channelId,UUID messageId);
    void addUserInChannel(UUID channelId, UUID userId);
}
