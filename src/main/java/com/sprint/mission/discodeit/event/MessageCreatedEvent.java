package com.sprint.mission.discodeit.event;

import java.util.UUID;

/**
 * 채널에 새로운 메시지가 등록된 후 발행되는 이벤트
 *
 * @param channelId 메시지가 등록된 채널 ID
 * @param messageId 등록된 메시지 ID
 */
public record MessageCreatedEvent(
    UUID channelId,
    UUID messageId
) {

}
