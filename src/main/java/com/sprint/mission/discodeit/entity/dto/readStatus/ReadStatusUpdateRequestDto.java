package com.sprint.mission.discodeit.entity.dto.readStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class ReadStatusUpdateRequestDto {
    private UUID statusId;
    private Instant nowTime;
}
