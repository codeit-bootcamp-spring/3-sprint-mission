package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "변경할 메세지 정보")
public final class MessageUpdateRequest {

  @Schema(description = "변경할 메세지 내용", example = "changeText")
  @NotBlank(message = "변경할 메세지 내용을 작성해주세요")
  private final String messageContent;
}
