package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageCreatedEvent(
    UUID messageId,           // DB에 저장된 Message PK
    UUID channelId,           // 메시지가 속한 채널 ID
    UUID authorId,            // 메시지 작성자 ID
    String content,           // 메시지 내용
    Instant createdAt,        // 메시지 생성 시각
    List<UUID> attachmentIds  // 첨부파일 ID 목록
) {

}
