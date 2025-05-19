package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {

  private final MessageService messageService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<MessageResponse> createMessage(
      @RequestBody MessageCreateRequest messageRequest,
      @RequestParam(name = "attachments", required = false) List<BinaryContentCreateRequest> attachments
  ) {
    return ResponseEntity.ok(
        messageService.create(messageRequest, attachments != null ? attachments : List.of()));
  }

  @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
  public ResponseEntity<MessageResponse> getMessage(@PathVariable UUID messageId) {
    return ResponseEntity.ok(messageService.find(messageId));
  }

  @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.GET)
  public ResponseEntity<List<Message>> getMessagesByChannel(@PathVariable UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
  public ResponseEntity<MessageResponse> updateMessage(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    return ResponseEntity.ok(messageService.update(messageId, request));
  }

  @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<MessageResponse> deleteMessage(@PathVariable UUID messageId) {
    return ResponseEntity.ok(messageService.delete(messageId));
  }
}

