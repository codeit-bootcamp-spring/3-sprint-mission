package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketRequiredEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    // 트랜잭션 커밋 후 전송
    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        MessageDto message = event.getData();
        // 클라이언트는 "/sub/channels.{channelId}.messages" 를 구독
        String destination = String.format("/sub/channels.%s.messages", message.channelId());
        messagingTemplate.convertAndSend(destination, message);
    }
}