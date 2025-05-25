package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채널 유형을 구분하는 모델")
public enum ChannelType {
  @Schema(description = "공개 채널")
  PUBLIC,
  @Schema(description = "비공개 채널")
  PRIVATE
}
