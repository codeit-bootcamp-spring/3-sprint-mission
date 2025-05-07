package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;

public record CreatePrivateChannelRequest (List<User> users) {
}
