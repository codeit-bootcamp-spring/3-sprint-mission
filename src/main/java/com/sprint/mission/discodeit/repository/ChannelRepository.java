package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    public Channel save(Channel channel);

    public Optional<Channel> findById(UUID channelId);

    public List<Channel> findAll();

    public boolean existsById(UUID channelId);

    public void deleteById(UUID channelId);

    // QUESTION : joinChannel, leavelChannel, readAttendees 도 repo에서 작성해야하나???
}
