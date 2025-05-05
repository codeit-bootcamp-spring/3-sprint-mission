package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository
 * fileName       : ChannelRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public interface ChannelRepository {
    Channel createPrivateChannelByName();
    Channel createPublicChannelByName(String name, String description);
    Channel findChannelById(UUID channelId);
    List<Channel> findAllChannel();
    void updateChannel(UUID channelId, String name);
    void deleteChannel(UUID channelId); // file | jcf : throw exception
}
