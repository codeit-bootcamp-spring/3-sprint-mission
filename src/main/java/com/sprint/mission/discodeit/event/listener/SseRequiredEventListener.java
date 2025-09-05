package com.sprint.mission.discodeit.event.listener;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.message.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.message.ChannelEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.message.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.message.UserEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

    private final SseService sseService;


    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(NotificationCreatedEvent event){
        sseService.send(event.getReceiverIds(),"notifications.created",event.getData());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(BinaryContentStatusUpdatedEvent event){
        sseService.broadcast("binaryContents.updated",event.data());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelEvent event){
        if(event.data().type().equals(ChannelType.PUBLIC)){
            sseService.broadcast(event.eventName(),event.data());
        }else{
            Set<UUID> receiverIds = event.data().participants().stream()
                .map(UserDto::id)
                .collect(Collectors.toSet());
            sseService.send(receiverIds, event.eventName(), event.data());
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserEvent event){
        sseService.broadcast(event.eventName(),event.data());
    }




}
