package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.payload.MessageCreatedEvent;
import com.sprint.mission.discodeit.service.MessageService;
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
    private final MessageService messageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        log.info("[WebSocketRequiredEventListener] WebSocket 메시지 전송 준비: {}", event);

        MessageDto messageDto = messageService.findLastMessageInChannel(event.channelId());
        if (messageDto == null) {
            log.warn("[WebSocketRequiredEventListener] 메시지 전송 실패: 채널 {}에서 메시지 조회 실패", event.channelId());
            return;
        }

        String destination = "/sub/channels." + event.channelId() + ".messages";
        messagingTemplate.convertAndSend(destination, messageDto);

        log.info("[WebSocketRequiredEventListener] WebSocket 전송 완료: 채널={}, 대상={}, 메시지ID={}",
            event.channelId(), destination, messageDto.id());
    }
}