package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // id = channel ID
    Channel create(PrivateChannelCreateDTO dto) throws IOException;
    Channel create(PublicChannelCreateDTO dto) throws IOException;
    Channel find(UUID id) throws IOException;
    List<Channel> findByName(String name);
    List<Channel> findAll();
    void addEntry(UUID id, UUID entryId);
    void addEntry(UUID id, List<UUID> entryIds);
//    Channel enterChannel(UUID user, UUID id) throws IOException;
    void updateName(UUID id, String name) throws IOException;
    void delete(UUID id) throws IOException;

}
