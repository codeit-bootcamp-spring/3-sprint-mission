package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.ChannelUpdateEvent;
import com.sprint.mission.discodeit.service.sse.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChannelUpdateEventListener {
    private final SseService sseService;

    @Async("channelTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelUpdateEvent event) {
        String eventName = switch (event.changeType()) {
            case CREATED -> "channels.created";
            case UPDATED -> "channels.updated";
            case DELETED -> "channels.deleted";
        };

        ChannelDto dto = event.dto();

        try {
            if (dto.type() == ChannelType.PUBLIC) {
                // 공개 채널이라면 전체 유저에게 이벤트 발송
                sseService.broadcast(eventName, dto);
            } else {
                // 비공개 채널이라면 참여자에게만 전송
                List<UUID> receiverIds = dto.participants().stream()
                    .map(UserDto::id)
                    .toList();

                if (!receiverIds.isEmpty()) {
                    sseService.send(receiverIds, eventName, dto);
                }
            }
            log.debug("[ChannelUpdateEventListener] {} 전송 성공: userId={}", eventName, event.dto().id());
        } catch (Exception e) {
            log.error("[ChannelUpdateEventListener] {} 전송 실패: userId={}", eventName, event.dto().id(), e);
        }
    }
}
