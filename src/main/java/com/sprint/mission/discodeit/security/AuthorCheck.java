package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("authorCheck")
@RequiredArgsConstructor
public class AuthorCheck {
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public boolean isMessageOwner(UUID messageId, UUID userId) {
        return messageRepository.existsByIdAndAuthorId(messageId, userId);
    }
}
