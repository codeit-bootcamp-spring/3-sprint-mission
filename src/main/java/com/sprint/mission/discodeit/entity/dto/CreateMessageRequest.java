package com.sprint.mission.discodeit.entity.dto;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<CreateBinaryContentRequest> attachments // 첨부파일 리스트
) { }
