package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChannelRequest(
    List<User> userList,
    UUID channelId
) {}
