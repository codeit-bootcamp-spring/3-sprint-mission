package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.DTO.Request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.service.DTO.Request.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;


public interface ChannelService {
//    Channel create(ChannelType type, String name, String description);
    Channel create(PublicChannelCreateRequest publicChannelCreateRequest);
    Channel create(PrivateChannelCreateRequest privateChannelCreateRequest);
    Channel find(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
}
