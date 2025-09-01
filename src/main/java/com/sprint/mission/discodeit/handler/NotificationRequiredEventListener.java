package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.jpa.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.basic.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : NotificationRequiredEventListener
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelService channelService;
    private final UserRepository userRepository;

    @Value("${discodeit.admin.username}") String adminUsername;

    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        MessageResponse message = event.getData();
        UUID channelId = message.channelId();
        ChannelResponse channel = channelService.findById(channelId);

        Set<UUID> receiverIds = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
                channelId)
            .stream().map(readStatus -> readStatus.getUser().getId())
            .filter(receiverId -> !receiverId.equals(message.author().id()))
            .collect(Collectors.toSet());
        String title = message.author().username()
            .concat(
                channel.getType().equals(ChannelType.PUBLIC) ?
                    String.format(" (#%s)", channel.getName()) : ""
            );
        String content = message.content();

        notificationService.create(receiverIds, title, content);
    }



}
