package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.FindChannelRespond;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;
import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel create(CreatePublicChannelRequest createPublicChannelRequest);
    Channel create(CreatePrivateChannelRequest createPrivateChannelRequest);
    List<FindChannelRespond> findAllByUserId(UUID userId);
    FindChannelRespond find(UUID id);
    void update(UpdateChannelRequest request);
    void delete(UUID channelId);
}
