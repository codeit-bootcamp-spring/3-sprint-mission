package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messageSecurity")
@RequiredArgsConstructor
public class MessageSecurity {

    private final MessageRepository messageRepository;

    /**
     * 주어진 메시지가 수정 또는 삭제 요청을 보낸 사용자 본인의 메시지인지 확인하는 메서드
     *
     * @param messageId 메시지  ID
     * @param userId    현재 로그인한 사용자 ID
     * @return 요청 사용자가 메시지 작성자인 경우 true, 그렇지 않으면 false
     */
    public boolean isOwner(UUID messageId, UUID userId) {
        return messageRepository.findById(messageId)
            .map(message -> message.getAuthor().getId().equals(userId))
            .orElse(false);
    }
}
