package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "변경하려는 채널 정보")
// 파라미터 그룹화를 위해 작성하였으나, PRIVATE CHANNEL의 구현체에서는 사용 X
public final class ChannelUpdateRequest {

  @Schema(description = "변경할 채널 ID", type = "string", format = "uuid")
  @NotBlank(message = "변경할 채널의 ID는 필수입니다")
  private UUID channelId;

  @Schema(description = "변경할 채널 설명", example = "이렇게 수정하고 싶어요")
  @NotBlank(message = "해당 채널의 변경하고자 하는 새로운 설명을 작성해주세요")
  private String description;

  @Schema(description = "변경할 채널명")
  @NotBlank(message = "변경하실 새로운 채널명을 작성해주세요")
  private String channelName;
}
