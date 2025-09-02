package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;

/**
 * BinaryContent 메타 정보가 DB에 정상적으로 저장된 후 발행되는 이벤트
 */
public record BinaryContentCreatedEvent(
    BinaryContent binaryContent,
    byte[] data
) {

}
