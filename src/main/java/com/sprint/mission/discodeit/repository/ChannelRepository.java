package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Map;
import java.util.UUID;

public interface ChannelRepository {

    public Map<UUID, Channel> loadFromFile();

    public void saveToFile(Map<UUID, Channel> channels);
}
