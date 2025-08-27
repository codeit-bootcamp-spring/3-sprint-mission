package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("resourceSecurity")
@RequiredArgsConstructor
@Slf4j
public class ResourceSecurityExpression {

    private final MessageService messageService;

    /**
     * 현재 사용자가 해당 사용자 리소스의 소유자인지 확인
     * */
    public boolean isOwner(UUID userid, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        return userDetails.getUserDto().id().equals(userid);
    }

    /**
     * 현재 사용자가 해당 메시지의 작성자인지 확인
     * */
    public boolean isMessageAuthor(UUID messageId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        try {
            DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
            UUID currentUserId = userDetails.getUserDto().id();

            // 메시지 정보를 조회하여 작성자 확인
            MessageDto message = messageService.find(messageId);
            return message.author() != null && message.author().id().equals(currentUserId);
        } catch (Exception e) {
            log.error("메시지 작성자 확인 중 오류 발생: messageId={}, error={}", messageId, e.getMessage());
            return false;
        }
    }
}
