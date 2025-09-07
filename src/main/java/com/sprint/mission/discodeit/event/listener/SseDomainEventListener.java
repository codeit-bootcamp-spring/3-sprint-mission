package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.UserCreatedEvent;
import com.sprint.mission.discodeit.event.UserDeletedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseDomainEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(NotificationCreatedEvent event) {
    sseService.send(List.of(event.notification().receiverId()),
        "notifications.created",
        event.notification());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelCreatedEvent event) {
    dispatchChannel("channels.created", event.channel());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelUpdatedEvent event) {
    dispatchChannel("channels.updated", event.channel());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelDeletedEvent event) {
    dispatchChannel("channels.deleted", event.channel());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserCreatedEvent event) {
    sseService.broadcast("users.created", event.user());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserUpdatedEvent event) {
    sseService.broadcast("users.updated", event.user());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserDeletedEvent event) {
    sseService.broadcast("users.deleted", event.user());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(BinaryContentStatusUpdatedEvent event) {
    // Owner information is not available from BinaryContentResponse; broadcast for now.
    sseService.broadcast("binaryContents.updated", event.content());
  }

  private void dispatchChannel(String name, ChannelResponse channel) {
    if (channel.type() == ChannelType.PRIVATE && channel.participants() != null) {
      List<UUID> receivers = channel.participants().stream()
          .map(UserResponse::id)
          .toList();
      sseService.send(receivers, name, channel);
    } else {
      sseService.broadcast(name, channel);
    }
  }
}

