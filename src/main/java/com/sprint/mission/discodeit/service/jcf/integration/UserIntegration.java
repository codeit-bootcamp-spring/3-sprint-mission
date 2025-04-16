package com.sprint.mission.discodeit.service.jcf.integration;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class UserIntegration {
    private final UserService userService;
    private final ChannelService channelService;

    public UserIntegration(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    public void deleteUser(UUID userId) {
        userService.deleteUser(userId);
        channelService.getAllChannels().forEach(channel -> channel.getUserIds().remove(userId));
    }
}
