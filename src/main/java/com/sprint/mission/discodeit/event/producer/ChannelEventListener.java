package com.sprint.mission.discodeit.event.producer;

import com.sprint.mission.discodeit.event.ChannelEvent;
import com.sprint.mission.discodeit.event.EventType;
import com.sprint.mission.discodeit.event.publisher.KafkaEventPublisher;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
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
public class ChannelEventListener {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final ChannelMapper channelMapper;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelEvent event) {
        String eventName = switch (event.getType()) {
            case CREATED -> "channels.created";
            case UPDATED -> "channels.updated";
            case DELETED -> "channels.deleted";
        };

        Object payload;
        if (event.getType() == EventType.DELETED) {
            payload = Map.of("id", event.getId());
        } else {
            payload = channelMapper.toDto(event.getEntity());
        }

        kafkaEventPublisher.publish(eventName, payload);
        log.info("[Kafka] {} 이벤트 발행: payload={}", eventName, payload);
    }

}
