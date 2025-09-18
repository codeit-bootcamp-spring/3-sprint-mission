package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("authGuard")
@RequiredArgsConstructor
public class AuthGuard {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;

    public boolean isSelf(UUID userId) {
        return userId.equals(authenticationFacade.getCurrentUserId());
    }

    public boolean isMessageOwner(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(message -> message.getAuthor().getId().equals(authenticationFacade.getCurrentUserId()))
                .orElse(false);
    }
}
