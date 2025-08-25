package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private static final String LISTENER_NAME = "[BinaryContentEventListener] ";

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
