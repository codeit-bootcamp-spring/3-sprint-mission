package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.channel.*;
import org.springframework.http.ResponseEntity;

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

    ChannelCreateResponse createChannel(PrivateChannelCreateRequest request);

    ChannelCreateResponse createChannel(PublicChannelCreateRequest request);

    UpdateChannelResponse update(UUID channelId, ChannelUpdateRequest request);

    boolean deleteChannel(UUID channelId);

    List<ChannelsFindResponse> findAllByUserId(UUID userId);

    //    ChannelFindResponse find(ChannelFindRequest request);
}
