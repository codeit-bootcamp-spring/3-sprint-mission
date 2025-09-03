package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messageSecurity")
@RequiredArgsConstructor
public class MessageSecurity {

    private final MessageRepository messageRepository;

    public boolean isOwner(UUID messageId, UUID userId) {
        return messageRepository.findById(messageId).map(m -> m.getAuthor().getId().equals(userId))
            .orElse(false);
    }
}
