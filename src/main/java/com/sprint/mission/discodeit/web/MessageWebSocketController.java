package com.sprint.mission.discodeit.web;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private static final String CONTROLLER_NAME = "[MessageWebSocketController] ";

    private final MessageService messageService;

    @MessageMapping("/messages") // /pub/messages
    public void sendMessage(MessageCreateRequest request) {

        log.info(CONTROLLER_NAME + "채널 ID: {}, 작성자 ID: {}, 내용: {}", request.channelId(), request.authorId(), request.content());

        messageService.create(request, List.of()); // 첨부파일 없는 메시지, MessageCreatedEvent 발행
    }
}
