package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "채널 정보를 담는 DTO")
public class ChannelDTO {

  // 채널 정보
  @Schema(description = "채널 ID", type = "string", format = "uuid")
  private UUID channelId;

  @Schema(description = "채널 이름", example = "개발자들의 쉼터", type = "string", format = "string")
  private String channelName;

  @Schema(description = "채널 유형", example = "PUBLIC")
  private ChannelType channelType;

  @Schema(description = "채널 설명", example = "개발자들의 소통 커뮤니티입니다")
  private String description;

  @Schema(description = "마지막 메세지 시간", type = "string", format = "date-time")
  private Instant lastestMessageAt;

  @Schema(description = "채널 참가자의 ID 목록", type = "string", format = "uuid")
  private List<UUID> participantIds;
}
