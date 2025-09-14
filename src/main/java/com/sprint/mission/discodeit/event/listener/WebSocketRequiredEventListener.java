package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.response.MessageDto;
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

    private final SimpMessagingTemplate messagingTemplate;

    // 메시지 생성 트랜잭션이 커밋된 직후, 채널 구독자들에게 메시지를 push
    // 경로: /sub/channels.{channelId}.messages
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {

        MessageDto messageDto = event.messageDto();
        String des = "/sub/channels." + messageDto.channelId() + ".messages";

        // 브로커로 브로드캐스트
        messagingTemplate.convertAndSend(des, messageDto);

        log.debug("[WebSocketRequiredEventListener] 메시지 브로드캐스트 완료 -> {} (messageId: {})", des, messageDto.id());
    }
}
