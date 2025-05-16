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

    ResponseEntity<ChannelCreateResponse> createChannel(PrivateChannelCreateRequest request);

    ResponseEntity<ChannelCreateResponse> createChannel(PublicChannelCreateRequest request);

    ChannelFindResponse find(ChannelFindRequest request);

    ResponseEntity<?> findAllByUserId(UUID userId);

    ResponseEntity<?> update(UUID channelId, ChannelUpdateRequest request);

    ResponseEntity<?> deleteChannel(UUID channelId);

}
