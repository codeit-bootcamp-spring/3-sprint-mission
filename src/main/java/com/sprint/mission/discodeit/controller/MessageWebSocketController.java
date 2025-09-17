package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    /**
     * 단순 텍스트 메시지 전송(STOMP) 클라이언트 -> /pub/messages 로 발행
     */
    @MessageMapping("messages")
    public void sendMessage(MessageCreateRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            return;
        }

        messageService.create(request, Collections.emptyList());
    }

}
