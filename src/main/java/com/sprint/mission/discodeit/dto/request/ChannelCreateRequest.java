package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.User;

public record ChannelCreateRequest(User creator, String name, String description) {

}
