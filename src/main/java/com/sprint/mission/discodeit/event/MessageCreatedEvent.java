package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;

/**
 * 메시지가 생성되었을 때 발생하는 이벤트입니다.
 * 
 * <p>새로운 메시지가 작성되었을 때 알림 전송이나 읽음 상태 업데이트 등의
 * 후속 작업을 위해 이벤트 리스너에게 알리는 데 사용됩니다.</p>
 * 
 * @param message 생성된 메시지 엔티티
 * @param occurredAt 이벤트 발생 시간
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
public record MessageCreatedEvent(
        Message message,
        Instant occurredAt
) {
}
