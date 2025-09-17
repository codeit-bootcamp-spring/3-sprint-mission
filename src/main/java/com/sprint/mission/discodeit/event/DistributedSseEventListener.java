package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.messaging.type", havingValue = "kafka")
public class DistributedSseEventListener {

    private final SseService sseService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 사용자 로그인/로그아웃 이벤트를 Kafka로 발행
     * */
    @Async("eventTaskExecutor")
    @EventListener
    public void handleUserLogInOutForKafka(UserLogInOutEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.UserLogInOutEvent", payload);
            log.info("[Kafka Producer] SSE 사용자 상태 이벤트 발행: userId = {}, isLoggedIn = {}",
                event.userId(), event.isLoggedIn());
        } catch (Exception e) {
            log.error("[Kafka Producer] SSE 사용자 상태 이벤트 발행 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * Kafka에서 사용자 상태 변경 이벤트 수신 및 SSE 전송
     */
    @KafkaListener(topics = "discodeit.UserLogInOutEvent")
    public void handleUserLogInOutFromKafka(String kafkaEvent) {
        try {
            UserLogInOutEvent event = objectMapper.readValue(kafkaEvent, UserLogInOutEvent.class);

            log.info("[Kafka Consumer] SSE 사용자 상태 이벤트 수신: userId = {}, isLoggedIn = {}",
                event.userId(), event.isLoggedIn());

            sseService.broadcast("user.status.changed", event);

            log.info("[SSE] 모든 클라이언트에게 사용자 상태 변경 알림 전송 완료: userId = {}, isLoggedIn = {}",
                event.userId(), event.isLoggedIn());

        } catch (Exception e) {
            log.error("[Kafka Consumer] SSE 사용자 상태 이벤트 처리 실패: {}", e.getMessage(), e);
        }
    }
}