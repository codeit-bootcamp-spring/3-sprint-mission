package com.sprint.mission.discodeit.refactor.repository;

import com.sprint.mission.discodeit.refactor.entity.Channel2;

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
public interface ChannelRepository2 {
    Channel2 createChannelByName(String name);
    Channel2 findChannelById(UUID channelId);
    List<Channel2> findAllChannel();
    void updateChannel(UUID channelId, String name);
    void deleteChannel(UUID channelId);
}
