package com.sprint.mission.discodeit.event;

import java.util.UUID;

/**
 * S3 파일 업로드 재시도가 모두 실패했을 때 발행되는 이벤트
 *
 * @param requestId       MDC의 Request ID
 * @param binaryContentId 업로드 실패한 바이너리 데이터 ID
 * @param errorMessage    에러 메시지
 */
public record S3UploadFailedEvent(
    String requestId,
    UUID binaryContentId,
    String errorMessage
) {

}
