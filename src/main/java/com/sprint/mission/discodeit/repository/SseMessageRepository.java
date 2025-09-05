package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {
    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public void save(SseMessage sseMessage) {
        eventIdQueue.add(sseMessage.getId());
        messages.put(sseMessage.getId(), sseMessage);
    }

    public List<SseMessage> getAfter(UUID lastEventId){
        boolean found = false;
        List<SseMessage> result = new ArrayList<>();
        if(lastEventId != null){
            for(UUID id:eventIdQueue){
                if(found){
                    result.add(messages.get(id));
                } else if(id.equals(lastEventId)){
                    found = true;
                }
            }
        }
        return result;
    }
}
