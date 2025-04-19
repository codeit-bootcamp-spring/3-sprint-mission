package com.sprint.mission.discodeit.v1.repository;

import com.sprint.mission.discodeit.v1.entity.Channel1;
import com.sprint.mission.discodeit.v1.entity.Message1;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository
 * fileName       : ChannelRepository
 * author         : doungukkim
 * date           : 2025. 4. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 16.        doungukkim       최초 생성
 */
public interface ChannelRepository1 {
    void saveChannel(Channel1 channel);
    Channel1 findChannelById(UUID channelId);
    List<Channel1> findChannelsByChannelIds(List<UUID> channelIds);
    List<Channel1> findAllChannels();
    void updateChannelNameById(UUID channelId, String title);
    void deleteChannelById(UUID channelId);
    void addMessageInChannel(UUID channelId, Message1 message);
    void deleteMessageInChannel(UUID channelId, UUID messageId);
    void addUserInChannel(UUID channelId, UUID userId);

}
