package com.sprint.mission.discodeit.event.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public <T> void publish(String topic, T eventData) {
        try {
            String payload = objectMapper.writeValueAsString(eventData);
            kafkaTemplate.send(topic, payload);
            log.debug("[KafkaEventPublisher] 이벤트 발행 완료: topic={}, payload={}", topic, payload);
        } catch (JsonProcessingException e) {
            log.error("[KafkaEventPublisher] 직렬화 실패: topic={}, error={}", topic, e.getMessage());
        }
    }
}


