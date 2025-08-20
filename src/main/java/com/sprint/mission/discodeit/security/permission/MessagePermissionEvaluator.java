package com.sprint.mission.discodeit.security.permission;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messagePermissionEvaluator")
@RequiredArgsConstructor
public class MessagePermissionEvaluator {

    private final MessageRepository messageRepository;

    public boolean isAuthor(UUID messageId, String username) {
        return messageRepository.findAuthorUsernameById(messageId)
            .map(authorUsername -> authorUsername.equals(username))
            .orElse(false);
    }
}
