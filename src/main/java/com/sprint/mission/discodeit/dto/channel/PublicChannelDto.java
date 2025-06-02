package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public Channel 생성 및 수정 정보")
public record PublicChannelDto(String name, String description) {

}
