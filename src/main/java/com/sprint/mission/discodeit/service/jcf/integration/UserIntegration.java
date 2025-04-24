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

    // 비즈니스 로직
    public void deleteUser(UUID userId) {
        userService.deleteUser(userId);
        channelService.getAllChannels().forEach(channel -> {
            if (channel.getUserIds().remove(userId)) {
                channelService.updateChannel(channel);
            }
        });
    }
}