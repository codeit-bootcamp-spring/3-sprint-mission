package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    /**
     * 클라이언트 → /pub/messages 로 전송
     */
    @MessageMapping("/messages")
    public void handleMessage(MessageCreateRequest request) {
        // 첨부파일이 없으므로 빈 리스트 전달
        MessageDto created = messageService.create(request, List.of());

        // 이벤트 발행은 BasicMessageService 내부에서 이미 수행됨
    }
}