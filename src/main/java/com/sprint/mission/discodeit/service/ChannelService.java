package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

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

    List<Channel> findAllChannel();

    ChannelFindResponse find(ChannelFindRequest request);

    List<ChannelFindResponse> findAllByUserId(ChannelFindByUserIdRequest request);

    void update(ChannelUpdateRequest request);

    void deleteChannel(UUID channelId);

}
