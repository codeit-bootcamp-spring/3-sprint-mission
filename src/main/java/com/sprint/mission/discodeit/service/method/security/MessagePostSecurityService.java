package com.sprint.mission.discodeit.service.method.security;

import com.sprint.mission.discodeit.repository.jpa.MessageRepository;
import com.sprint.mission.discodeit.service.basic.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.service.method.security
 * FileName     : MessagePostSecurityService
 * Author       : dounguk
 * Date         : 2025. 8. 7.
 */
@Component("MessagePostSecurityService")
@RequiredArgsConstructor
public class MessagePostSecurityService {
//    메시지 수정, 삭제는 해당 메시지를 작성한 사람만
    private final MessageRepository messageRepository;

    public boolean isAuthor(UUID messageId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = ((DiscodeitUserDetails) authentication.getPrincipal()).getUser().id();

        UUID authorId = messageRepository.findById(messageId).stream()
            .map(message -> message.getAuthor().getId()).findFirst().get();

        return authorId.equals(userId);
    }
}
