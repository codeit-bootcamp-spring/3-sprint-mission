package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/* 가장 최근 메세지 시간 정보. PRIVATE 채널이면, DTO에 참여한 User의 id 를 포함할것 */
public record ChannelDto(UUID id, ChannelType type, String name, String description, UUID ownerId,
                         List<UUID> participantIds,
                         Instant lastMessageAt) {

}