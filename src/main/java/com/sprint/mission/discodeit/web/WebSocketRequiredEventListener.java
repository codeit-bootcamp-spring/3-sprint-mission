package com.sprint.mission.discodeit.web;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
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

    private static final String LISTENER_NAME = "[WebSocketRequiredEventListener] ";
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트가 채널 입장 시 웹소켓으로 /sub/channels.{channelId}.messages를 구독해 메시지 수신하기 때문에,
    // 메시지가 생성되면 해당 엔드포인트로 메시지 보냄
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {

        MessageDto payload = event.message();
        String dest = "/sub/channels." + payload.channelId() + ".messages"; // /sub/channels.{channelId}.messages

        messagingTemplate.convertAndSend(dest, payload);
        log.info(LISTENER_NAME + "{}으로 메시지 핸들링, messageId={}", dest, payload.id());
    }
}
