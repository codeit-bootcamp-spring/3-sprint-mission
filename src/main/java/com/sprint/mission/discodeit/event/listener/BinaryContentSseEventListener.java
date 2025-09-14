package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.service.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentSseEventListener {
    private final SseService sseService;

    // 커밋 완료 후 실행
    @Async("fileTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(BinaryContentUpdatedEvent event) {
        try{
            sseService.broadcast("binaryContents.updated", event.dto());
        } catch (Exception e) {
            log.error("[BinaryContentSseEventListener] SSE 전송 실패: binaryContents.updated 이벤트, dto= {}", event.dto(), e);
        }
    }

}
