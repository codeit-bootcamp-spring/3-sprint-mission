package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
// 파라미터 그룹화를 위해 작성하였으나, PRIVATE CHANNEL의 구현체에서는 사용 X
public class ChannelUpdateRequest {
    private UUID channelId;
    private String description;
    private String channelName;
}
