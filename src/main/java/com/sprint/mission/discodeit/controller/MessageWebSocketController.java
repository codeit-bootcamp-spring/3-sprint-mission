package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    @MessageMapping("/messages")
    public MessageResponseDto create(@Payload MessageRequestDto messageCreateRequest) {

        log.debug("[MessageWebSocketController] 메시지 전송 요청 - channelId: {}, authorId: {}",
            messageCreateRequest.channelId(), messageCreateRequest.authorId());

        MessageResponseDto message = messageService.create(messageCreateRequest,
            Collections.emptyList());

        log.debug(
            "[MessageWebSocketController] 메시지 전송 성공 - channelId: {}, authorId: {}, content: {}",
            message.channelId(), message.author().id(), message.content());

        return message;
    }
}
