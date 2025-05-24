package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entitiy.User;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "PrivateChannel 생성 정보")
public record PrivateChannelCreateRequest(List<UUID> participantIds) {

}
