package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

/**
 * 바이너리 콘텐츠 관련 이벤트를 처리하는 리스너 클래스입니다.
 * 
 * <p>BinaryContentCreatedEvent를 수신하여 실제 파일 데이터를 스토리지에 저장하고,
 * 처리 상태를 업데이트합니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>비동기 처리로 성능 최적화</li>
 *   <li>트랜잭션 완료 후 이벤트 처리</li>
 *   <li>파일 저장 실패 시 상태 관리</li>
 *   <li>파일 전용 스레드 풀 사용</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private static final String LISTENER_NAME = "[BinaryContentEventListener] ";

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    /**
     * 바이너리 콘텐츠 생성 이벤트를 처리합니다.
     * 
     * <p>트랜잭션이 커밋된 후 비동기적으로 실행되며, 실제 파일 데이터를
     * 스토리지에 저장하고 처리 상태를 업데이트합니다.</p>
     * 
     * @param event 바이너리 콘텐츠 생성 이벤트
     */
    @Async("fileTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {
        BinaryContent binaryContent = event.binaryContent();
        UUID id = binaryContent.getId();
        byte[] bytes = event.content();

        if (bytes == null || bytes.length == 0) {
            log.warn(LISTENER_NAME + "빈 컨텐츠는 저장하지 않습니다: id={}", id);
            binaryContentService.updateStatus(id, BinaryContentStatus.FAIL);
            return;
        }

        log.info(LISTENER_NAME + "BinaryContent bytes 저장 시작: id={}, size={}", id, bytes.length);

        try {
            binaryContentStorage.put(id, bytes);
            binaryContentService.updateStatus(id, BinaryContentStatus.SUCCESS);
            log.info(LISTENER_NAME + "BinaryContent bytes 저장 완료: id={}", id);
        } catch (Exception e) {
            log.error(LISTENER_NAME + "BinaryContent bytes 저장 실패: id={}", id, e);
            // 실패한 경우의 처리 로직을 추가할 수 있습니다.
            // 예: 재시도 큐에 추가, 알림 발송 등
            binaryContentService.updateStatus(id, BinaryContentStatus.FAIL);
        }
    }
}
