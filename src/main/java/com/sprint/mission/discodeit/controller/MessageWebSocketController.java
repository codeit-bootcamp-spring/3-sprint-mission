package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")
  public void sendMessage(@Payload MessageCreateRequest request) {
    CreateMessageCommand command = new CreateMessageCommand(
        request.content(),
        request.authorId(),
        request.channelId(),
        Collections.emptyList()
    );
    messageService.create(command);
  }
}
