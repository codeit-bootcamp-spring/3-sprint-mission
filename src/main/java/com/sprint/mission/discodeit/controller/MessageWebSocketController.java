package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    /**
     * 클라이언트 -> 서버: /pub/messages
     * 클라이언트가 메시지 보낼 때 STOMP 경로를 이용한다.
     */
    @MessageMapping("/messages")
    public void sendMessage(@Payload MessageCreateRequest request) {
        MessageDto messageDto = messageService.create(request, null);
    }
}
