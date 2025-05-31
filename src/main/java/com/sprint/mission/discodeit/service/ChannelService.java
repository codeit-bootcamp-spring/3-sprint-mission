package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

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

    JpaChannelResponse createChannel(PrivateChannelCreateRequest request);

    JpaChannelResponse createChannel(PublicChannelCreateRequest request);

    JpaChannelResponse update(UUID channelId, ChannelUpdateRequest request);

    boolean deleteChannel(UUID channelId);

    List<JpaChannelResponse> findAllByUserId(UUID userId);

    //    ChannelFindResponse find(ChannelFindRequest request);
}
