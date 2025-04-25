package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
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
    // 삭제예정
    //    Channel createChannel(String name);

    Channel createChannel(PrivateChannelCreateRequest request);
    Channel createChannel(PublicChannelCreateRequest request);


    Channel findChannelById(UUID channelId);

    List<Channel> findAllChannel();

    void updateChannel(UUID channelId, String name);

    void deleteChannel(UUID channelId);
}
