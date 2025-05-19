package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "생성할 공개 채널 정보")
public final class PublicChannelCreateRequest {

  @Schema(description = "생성할 채널명", example = "개발자 커뮤니티")
  @NotBlank(message = "생성할 채널의 이름은 필수입니다")
  private String channelName;

  @Schema(description = "생성할 채널의 설명", example = "개발자들의 소통 채널입니다")
  @NotBlank(message = "생성할 채널의 설명을 작성해주세요")
  private String description;
}
