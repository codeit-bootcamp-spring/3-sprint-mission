package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

//수정할 값 파라미터

public record MessageUpdateRequest(String newContent, List<UUID> attachmentIds) {
}

