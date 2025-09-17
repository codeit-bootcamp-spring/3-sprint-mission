package com.sprint.mission.discodeit.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTopicListener {

    private final ObjectMapper objectMapper;
    private final SseService sseService;

    @KafkaListener(topics = {"users.created", "users.updated", "users.deleted"})
    public void onUserEvent(String kafkaEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            Object payload = objectMapper.readValue(kafkaEvent, Object.class);
            sseService.broadcast(topic, payload);
            log.info("[Kafka] {} 브로드캐스트 완료: {}", topic, payload);
        } catch (Exception e) {
            log.error("[Kafka] {} 처리 실패", topic, e);
        }
    }

    @KafkaListener(topics = {"channels.created", "channels.updated", "channels.deleted"})
    public void onChannelEvent(String kafkaEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            Object payload = objectMapper.readValue(kafkaEvent, Object.class);
            sseService.broadcast(topic, payload);
            log.info("[Kafka] {} 브로드캐스트 완료: {}", topic, payload);
        } catch (Exception e) {
            log.error("[Kafka] {} 처리 실패", topic, e);
        }
    }
}
