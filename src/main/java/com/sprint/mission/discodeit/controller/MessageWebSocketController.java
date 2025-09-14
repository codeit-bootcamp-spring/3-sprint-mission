package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * 첨부파일이 없는 단순 텍스트 메시지인 경우 STOMP를 통해 메시지를 전송할 수 있도록 컨트롤러를 구현
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    // setApplicationDestinationPrefixes에서 설정한 경로 (/pub)으로 들어오는 /messages요청 받아서 처리
    @MessageMapping("/messages")
    public void sendMessage(
        @Valid @Payload MessageCreateRequest request
    )
    {
        log.info("[MessageWebSocketController] 단순 텍스트 메시지 STOMP로 전송 시작");
        MessageDto created = messageService.create(request, Collections.emptyList());
    }
}
