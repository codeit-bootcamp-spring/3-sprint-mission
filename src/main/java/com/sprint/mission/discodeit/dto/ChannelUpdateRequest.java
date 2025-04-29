package com.sprint.mission.discodeit.dto;

import java.util.UUID;

//수정 대상 객체의 id 파라미터, 수정할 값 파라미터
public record ChannelUpdateRequest(UUID channelId, String newName, String newDescription) {
}

