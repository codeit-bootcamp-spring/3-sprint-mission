package com.sprint.mission.discodeit.event.producer;

import com.sprint.mission.discodeit.event.EventType;
import com.sprint.mission.discodeit.event.UserEvent;
import com.sprint.mission.discodeit.event.publisher.KafkaEventPublisher;
import com.sprint.mission.discodeit.mapper.UserMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final UserMapper userMapper;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserEvent event) {
        String eventName = switch (event.getType()) {
            case CREATED -> "users.created";
            case UPDATED -> "users.updated";
            case DELETED -> "users.deleted";
        };

        Object payload = (event.getType() == EventType.DELETED)
            ? Map.of("id", event.getId())
            : userMapper.toDto(event.getEntity());

        kafkaEventPublisher.publish(eventName, payload);
        log.info("[Kafka] {} 이벤트 발행: payload={}", eventName, payload);
    }

}
