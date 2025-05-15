package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageDTO> createMessage(@RequestBody CreateMessageRequest createMessageRequest) {
        Message message = messageService.create(createMessageRequest);
        return ResponseEntity.ok(MessageDTO.fromDomain(message));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<MessageDTO> find(@RequestParam("id") UUID messageId) {
        Message message = messageService.find(messageId);
        return ResponseEntity.ok(MessageDTO.fromDomain(message));
    }

    @RequestMapping(value = "{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageDTO>> findAllMessageByChannelId(@PathVariable("channelId") UUID channelId) {
        List<Message> messageList = messageService.findAllByChannelId(channelId);
        List<MessageDTO> messageDTO = messageList.stream()
                .map(MessageDTO::fromDomain)
                .toList();
        return ResponseEntity.ok(messageDTO);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<MessageDTO> updateMessage(@RequestBody UpdateMessageRequest updateMessageRequest) {
        Message message = messageService.update(updateMessageRequest);
        return ResponseEntity.ok(MessageDTO.fromDomain(message));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMessage(@RequestParam("id") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.ok("메시지 ID : " + messageId + "삭제 완료");
    }


}
