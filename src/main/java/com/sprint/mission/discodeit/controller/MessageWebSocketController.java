package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages")
    public void handleTextMessage(@Valid MessageCreateRequest request) {
        log.info("[MessageWebSocketController] 메시지 수신: 채널={}, 내용={}", request.channelId(), request.content());

        MessageDto saved = messageService.create(request, List.of());

        String destination = "/sub/channels/" + request.channelId();
        messagingTemplate.convertAndSend(destination, saved);

        log.info("[MessageWebSocketController] 메시지 전송 완료: {}", destination);
    }
}