package com.sprint.mission.discodeit.event.listener;


import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.message.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.message.ChannelEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.message.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.message.UserEvent;
import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.SseService;
import java.util.Optional;
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
    private final UserRepository userRepository;
    private final BinaryContentMapper binaryContentMapper;

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

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserLogInOutEvent event){
        User user = userRepository.findById(event.userId()).get();
        BinaryContentDto profile = binaryContentMapper.toDto(user.getProfile());
        UserDto data = new UserDto(user.getId(), user.getUsername(), user.getEmail(),
            profile, event.online(), user.getRole());
        sseService.broadcast("users.updated",data);
    }




}
