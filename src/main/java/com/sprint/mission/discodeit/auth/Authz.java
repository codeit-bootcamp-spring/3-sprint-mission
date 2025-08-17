package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class Authz {

    private final MessageRepository messageRepository;

    /** 현재 로그인 사용자의 UUID 반환(없으면 null) */
    private UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof DiscodeitUserDetails dud) {
            return dud.getUserDto().id();
        }
        return null;
    }

    /** 사용자 정보 수정/삭제: 본인 여부 확인 */
    public boolean isMe(UUID userId) {
        UUID me = currentUserId();
        return me != null && me.equals(userId);
    }

    /** 메시지 수정/삭제: 작성자 여부 확인 */
    public boolean isMessageAuthor(UUID messageId) {
        UUID me = currentUserId();
        if (me == null) return false;
        return messageRepository.findById(messageId)
            .map(m -> me.equals(m.getAuthor().getId()))
            .orElse(false);
    }
}