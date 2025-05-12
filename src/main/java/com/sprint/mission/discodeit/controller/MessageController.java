package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

  private final MessageService messageService;

  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    List<BinaryContentCreateRequest> binaryContentCreateRequests =
        Optional.ofNullable(attachments)
            .orElse(List.of()).stream()
            .map(this::resolveAttachmentRequest)
            .flatMap(Optional::stream)
            .toList();

    Message message = messageService.create(messageCreateRequest,
        binaryContentCreateRequests);
    return ResponseEntity.created(URI.create("/api/message/" + message.getId())).body(message);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Message>> searchMessages(
      @RequestParam(required = false) UUID channelId,
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) String content
  ) {
    return ResponseEntity.ok(messageService.searchMessages(channelId, userId, content));
  }

  @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.GET)
  public ResponseEntity<List<Message>> findAllByChannelId(@PathVariable UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
  public ResponseEntity<Message> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    return messageService.updateContent(messageId, request)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<Object> delete(@PathVariable UUID messageId) {
    return messageService.delete(messageId)
        .map(m -> ResponseEntity.noContent().build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private Optional<BinaryContentCreateRequest> resolveAttachmentRequest(MultipartFile attachments) {

    if (attachments.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            attachments.getOriginalFilename(),
            attachments.getContentType(),
            attachments.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
