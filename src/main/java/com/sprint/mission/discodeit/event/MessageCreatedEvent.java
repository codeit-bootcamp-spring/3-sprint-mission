package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public record MessageCreatedEvent(
    User author,
    Channel channel,
    String content
) {

}
