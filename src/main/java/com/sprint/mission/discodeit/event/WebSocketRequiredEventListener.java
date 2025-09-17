package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.messaging.type", havingValue = "local", matchIfMissing = true)
public class WebSocketRequiredEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreateEvent event) {
        var channelId = event.channelId();
        var messageId = event.messageId();

        try {
            // 메시지 전체 정보를 조회하여 WebSocket으로 전송
            MessageDto messageDto = messageService.find(messageId);

            // 채널을 구독하고 있는 클라이언트들에게 메시지 전송
            String destination = "/sub/channels." + channelId + ".messages";
            messagingTemplate.convertAndSend(destination, messageDto);

            log.info("WebSocket으로 메시지 전송 완료 : channelId = {}, messageId = {}, destination = {} "
                , channelId, messageId, destination);

        } catch (Exception e) {
            log.error("WebSocket 메시지 전송 실패 : channelId = {}, messageId = {}, error= {}"
                , channelId, messageId, e.getMessage(),e);
        }
    }
}