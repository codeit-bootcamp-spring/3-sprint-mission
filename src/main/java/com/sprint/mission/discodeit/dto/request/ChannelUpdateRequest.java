package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(description = "변경하려는 채널 정보")
// 파라미터 그룹화를 위해 작성하였으나, PRIVATE CHANNEL의 구현체에서는 사용 X
public final class ChannelUpdateRequest {

  @Schema(description = "변경할 채널명")
  private String newName;

  @Schema(description = "변경할 채널 설명", example = "이렇게 수정하고 싶어요")
  private String newDescription;
}
