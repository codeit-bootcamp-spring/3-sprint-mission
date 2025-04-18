package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service.jcf
 * fileName       : ChannelService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public interface ChannelService {
    Channel createChannel(String name);
    Channel findChannelById(UUID channelId);
    List<Channel> findAllChannel();
    void updateChannel(UUID channelId, String name);
    void deleteChannel(UUID channelId);
}
