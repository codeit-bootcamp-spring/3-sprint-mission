package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageWebSocketController {

    private final MessageService messageService;

    @MessageMapping("/messages")
    public void sendMessage(MessageCreateRequest request) {
        log.info("WebSocket으로 메시지 수신 : channelId = {}, authorId = {}",
            request.channelId(), request.authorId());

        // 요구사항에 따라 첨부파일이 없는 단순 텍스트 메시지만 처리
        messageService.create(request, null);
    }
}