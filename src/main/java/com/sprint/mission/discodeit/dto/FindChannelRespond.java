package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entitiy.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FindChannelRespond(String channelName, String description, ChannelType channelType, Instant latestMessageTime,
                                 List<UUID> privateChannelUsersId) {
}
