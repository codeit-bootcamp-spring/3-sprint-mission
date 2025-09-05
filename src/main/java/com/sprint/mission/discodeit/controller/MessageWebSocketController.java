package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class MessageWebSocketController {

    private static final String CONTROLLER_NAME = "[MessageWebSocketController] ";

    @MessageMapping("/pub/messages")
    @SendTo
    public MessageCreatedEvent sendMessage(MessageCreatedEvent messageCreatedEvent) {

        log.info(CONTROLLER_NAME + "메시지: {}", messageCreatedEvent.message().content());
        return messageCreatedEvent;
    }
}
