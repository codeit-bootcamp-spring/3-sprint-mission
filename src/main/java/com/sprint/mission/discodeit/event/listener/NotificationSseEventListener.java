package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.event.NotificationsPersistedEvent;
import com.sprint.mission.discodeit.service.sse.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationSseEventListener {

    private final SseService sseService;

    // 커밋 완료 후 실행
    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(NotificationsPersistedEvent event) {

        List<UUID> receivers = event.receiverIds();
        if (receivers == null || receivers.isEmpty()) {
            return;
        }

        // 알림을 수신자들한테 push
        for (NotificationDto dto : event.notifications()) {
            try {
                sseService.send(event.receiverIds(), "notifications.created", dto);
            } catch (Exception e) {
                log.error("[NotificationSseEventListener] SSE 전송 실패: notifications.created 이벤트, dto= {}, receivers= {}", dto, receivers, e);
            }
        }
    }
}
