package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Collections;

/**
 * PackageName  : com.sprint.mission.discodeit.controller
 * FileName     : MessageWebSocketController
 * Author       : dounguk
 * Date         : 2025. 9. 3.
 */
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;


    @MessageMapping("/messages")
    public void sendTextMessage(@Payload MessageCreateRequest request) {

        if (request.content() == null || request.content().trim().isEmpty()) {
            return;
        }

        messageService.create(request, Collections.<BinaryContentCreateRequest>emptyList());
    }
}
