package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "생성할 비공개 채널 정보")
public final class PrivateChannelCreateRequest {

  // 2개의 속성 제거( name, description )
  @Schema(description = "비공개 채널에 참여할 사용자 ID 목록")
  private List<UUID> userIds;
}
