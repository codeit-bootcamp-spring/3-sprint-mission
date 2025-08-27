package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;

/**
 * 바이너리 콘텐츠가 생성되었을 때 발생하는 이벤트입니다.
 * 
 * <p>파일 업로드가 완료되었을 때 파일 저장소에 실제 파일을 저장하기 위해
 * 이벤트 리스너에게 알리는 데 사용됩니다.</p>
 * 
 * @param binaryContent 생성된 바이너리 콘텐츠 엔티티
 * @param content 실제 파일 데이터 (바이트 배열)
 * @param occurredAt 이벤트 발생 시간
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
public record BinaryContentCreatedEvent(
        BinaryContent binaryContent,
        byte[] content,
        Instant occurredAt
) {

}
