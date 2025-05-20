package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> createMessage(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    System.out.println("Message Controller create hit");
    System.out.println(">> request: " + messageRequest);
    MessageResponse response = messageService.create(
        messageRequest,
        attachments != null ? attachments : List.of()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }


  @GetMapping
  public ResponseEntity<List<Message>> getMessagesByChannel(
      @RequestParam UUID channelId
  ) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageResponse> updateMessage(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    return ResponseEntity.ok(messageService.update(messageId, request));
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }
}
