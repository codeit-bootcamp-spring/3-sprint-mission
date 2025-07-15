package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;

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

    ChannelResponse createChannel(PublicChannelCreateRequest request);

    ChannelResponse createChannel(PrivateChannelCreateRequest request);

    ChannelResponse update(UUID channelId, ChannelUpdateRequest request);

    void deleteChannel(UUID channelId);

    List<ChannelResponse> findAllByUserId(UUID userId);
}
