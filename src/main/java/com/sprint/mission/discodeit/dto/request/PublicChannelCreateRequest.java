package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공개 채널 생성 요청 DTO")
public record PublicChannelCreateRequest(
        @Schema(description = "채널 이름", example = "일반-채팅", minLength = 1, maxLength = 100)
        String name,

        @Schema(description = "채널 설명", example = "일반적인 대화를 나누는 채널입니다", nullable = true)
        String description
) {}
