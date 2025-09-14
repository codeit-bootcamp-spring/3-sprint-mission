package com.sprint.mission.discodeit.repository.sse;

import com.sprint.mission.discodeit.entity.SseMessage;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 이벤트 유실 복원을 위해 SSE 메시지를 저장하는 컴포넌트
 */
@Repository
public class SseMessageRepository {
    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    // 스레드 안전하게 순서 중가하기 위함
    private final AtomicLong sequence = new AtomicLong();

    // 새 이벤트 저장 (수신자 다수)
    public SseMessage saveEvent(Collection<UUID> receivers, String eventName, Object data) {
        UUID id = UUID.randomUUID();
        long seq = sequence.incrementAndGet();
        Set<UUID> receiverSet;

        if (receivers == null || receivers.isEmpty()) {
            receiverSet = Set.of();
        }
        else {
            receiverSet = Set.copyOf(receivers);
        }

        SseMessage sseMessage = new SseMessage(id, seq, eventName, data, receiverSet, Instant.now());

        messages.put(id, sseMessage);
        eventIdQueue.addLast(id);

        return sseMessage;
    }

    // 새 이벤트 저장 (수신자 한 명)
    public SseMessage saveEvent(UUID receiver, String eventName, Object data) {
        UUID id = UUID.randomUUID();
        long seq = sequence.incrementAndGet();
        Set<UUID> receiverSet;

        if (receiver == null) {
            receiverSet = Set.of();
        }
        else {
            receiverSet = Set.of(receiver);
        }

        SseMessage sseMessage = new SseMessage(id, seq, eventName, data, receiverSet, Instant.now());

        messages.put(id, sseMessage);
        eventIdQueue.addLast(id);

        return sseMessage;
    }

    // 브로드 캐스트 저장
    public SseMessage appendBroadcast(String eventName, Object data) {
        UUID id = UUID.randomUUID();
        long seq = sequence.incrementAndGet();
        Set<UUID> receiverSet = Set.of();

        SseMessage sseMessage = new SseMessage(id, seq, eventName, data, receiverSet, Instant.now());

        messages.put(id, sseMessage);
        eventIdQueue.addLast(id);

        return sseMessage;
    }

    // 메시지 조회 (LastEventId 이후 메시지 조회)
    public List<SseMessage> findMissedMessage(UUID userId, UUID lastEventId) {
        List<SseMessage> result = new ArrayList<>();

        // lastEventId 이후인지 찾기 위한 변수
        boolean after = false;

        for (UUID id : eventIdQueue) {
            if (!after) {
                if (id.equals(lastEventId)) {
                    after = true;
                }
                continue;
            }

            SseMessage sseMessage = messages.get(id);
            if (sseMessage == null) {
                continue;
            }
            // 수신자가 전체인 브로드캐스트 이거나 내가 받아야 하는 이벤트인지 확인
            if (sseMessage.receivers().isEmpty() || sseMessage.receivers().contains(userId)) {
                result.add(sseMessage);
            }
        }

        return result;
    }

    // 조회된 메시지 전송
    public int send(UUID userId, UUID lastEventId, SseEmitter emitter) {
        List<SseMessage> missed = findMissedMessage(userId, lastEventId);

        // lastEventId 이후 메시지 몇 개 보냈는지 로깅용 변수
        int sent = 0;

        for (SseMessage message : missed) {
            try {
                emitter.send(
                    SseEmitter.event()
                        .id(message.id().toString())
                        .name(message.eventName())
                        .data(message.data())
                );
                sent++;
            } catch (IOException e) {
                try {
                    emitter.completeWithError(e);
                } catch (Exception ignore) {}
                break;
            }
        }
        return sent;
    }
}
