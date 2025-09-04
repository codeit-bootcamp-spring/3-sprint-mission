package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {

        UUID channelId = event.data().channelId();

        String destination = "/sub/channels." + channelId + ".messages";

        log.debug("[WebSocketRequiredEventListener] 브로드캐스트 - {} payload: {}", destination,
            event.data());

        messagingTemplate.convertAndSend(destination, event.data());
    }
}
