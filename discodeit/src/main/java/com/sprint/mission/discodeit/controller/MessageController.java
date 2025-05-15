package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping
  public ResponseEntity<Message> createMessage(
      @RequestBody CreateMessageRequest createMessageRequest) {
    Message message = messageService.create(createMessageRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(message);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<MessageDTO> find(@PathVariable("id") UUID messageId) {
    Message message = messageService.find(messageId);
    return ResponseEntity.ok(MessageDTO.fromDomain(message));
  }

  @GetMapping
  public ResponseEntity<List<Message>> findAllMessageByChannelId(
      @RequestParam("channelId") UUID channelId) {
    List<Message> messageList = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messageList);
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> updateMessage(@PathVariable("messageId") UUID messageId,
      @RequestBody UpdateMessageRequest updateMessageRequest) {
    Message message = messageService.update(messageId, updateMessageRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(message);
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<String> deleteMessage(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body("메시지 ID : " + messageId + " 삭제 성공");
  }


}
