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
    //  input == null
    Channel createChannel(String name);
    //  input == null
    // channel == null
    Channel findChannelById(UUID channelId);
    //  channel == null
    List<Channel> findAllChannel();
    // input  == null
    // channel == null
    void updateChannel(UUID channelId, String name);
    // input == null
    void deleteChannel(UUID channelId);
}
