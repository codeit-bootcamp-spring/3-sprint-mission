package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 업로드가 실패했을 때 발생하는 이벤트입니다.
 * 
 * <p>파일 업로드 중 오류가 발생했을 때 실패 원인과 함께 이벤트 리스너에게
 * 알리는 데 사용됩니다.</p>
 * 
 * @param requestId 요청 식별자
 * @param binaryContentId 실패한 바이너리 콘텐츠의 ID
 * @param reason 업로드 실패 원인
 * @param occurredAt 이벤트 발생 시간
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
public record BinaryContentNotUploadedEvent (
        String requestId,
        UUID binaryContentId,
        String reason,
        Instant occurredAt
){
}
