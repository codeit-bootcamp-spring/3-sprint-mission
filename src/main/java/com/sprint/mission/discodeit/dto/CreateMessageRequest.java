package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entitiy.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record CreateMessageRequest(UUID channelId, UUID authorId, String text) {}
