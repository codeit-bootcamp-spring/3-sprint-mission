package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    public Channel write(Channel channel);

    public Channel read(UUID channelId);

    public List<Channel> readAll();

    public void delete(UUID channelId);

    // QUESTION : joinChannel, leavelChannel, readAttendees 도 repo에서 작성해야하나???
}
