package com.sprint.mission.discodeit.event.consumer;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
//@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        MessageDto payload = event.messageDto();
        UUID channelId = event.channelId();

        String destination = String.format("/sub/channels.%s.messages", channelId);
        log.info("[WebSocket] 메시지 브로드캐스트: channelId={}, destination={}",
            channelId, destination);

        messagingTemplate.convertAndSend(destination, payload);
    }

}
