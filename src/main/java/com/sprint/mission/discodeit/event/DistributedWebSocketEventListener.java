package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.messaging.type", havingValue = "kafka")
public class DistributedWebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 메시지 생성 이벤트를 Kafak로 발행
     * */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageCreateForKafka(MessageCreateEvent event) {
        try {
            // Kafka 토픽으로 이벤트 발행 ( 모든 인스턴스가 받게 )
            kafkaTemplate.send("message-created", event);
            log.info("Kafka로 메시지 생성 이벤트 발행 : channelId = {}, messageId = {}",
                event.channelId(), event.messageId());
        } catch (Exception e) {
            log.error("Kafka 메시지 발행 실패 : {} ", e.getMessage());
        }
    }

    /**
     * Kafka에서 메시지 생성 이벤트 수신 및 WebSocket 전송
     * */
    @KafkaListener(topics = "message-created")
    public void handleMessageCreateFromKafka(MessageCreateEvent event) {
        var channelId = event.channelId();
        var messageId = event.messageId();

        try {
            // 메시지 전체 정보를 조회하여 WebSocket으로 전송
            MessageDto messageDto = messageService.find(messageId);

            // 채널을 구독하고 있는 클라이언트들에게 메시지 전송
            String destination = "/sub/channels." + channelId + ".messages";
            messagingTemplate.convertAndSend(destination, messageDto);

            log.info("Kafka에서 수신한 이벤트로 WebSocke 메시지 전송 완료 : channelId = {}, messageId = {}, destination = {} ",
                channelId, messageId, destination);
        } catch (Exception e) {
            log.error("Kafka 이벤트 처리 실패 : channelId = {}, messageId = {}, error= {}",
                channelId, messageId, e.getMessage(),e);
        }
    }
}
