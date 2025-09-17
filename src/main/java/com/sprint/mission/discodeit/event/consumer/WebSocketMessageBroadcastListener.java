package com.sprint.mission.discodeit.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class WebSocketMessageBroadcastListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent", id = "message-${random.uuid}")
    public void onMessageCreated(String kafkaEvent) {
        log.info("[WebSocketMessageBroadcastListener] MessageCreatedEvent 토픽 구독 - 메시지 브로드캐스팅 시작");

        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
            String destination = String.format("/sub/channels.%s.messages", event.channelId());
            messagingTemplate.convertAndSend(destination, event.messageDto());
            log.info("[WebSocket] 메시지 브로드캐스트 완료: dest={}, messageId={}", destination, event.messageId());

        } catch (Exception e) {
            log.error("[WebSocket] MessageCreatedEvent 처리 실패", e);
        }
    }

}
