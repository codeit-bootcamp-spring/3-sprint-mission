package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

//수정 대상 객체의 id 파라미터, 수정할 값 파라미터
//QUESTION.  attachmentIds를 수정할수있나?
public record MessageUpdateRequest(UUID messageId, String newContent, List<UUID> attachmentIds) {
}

