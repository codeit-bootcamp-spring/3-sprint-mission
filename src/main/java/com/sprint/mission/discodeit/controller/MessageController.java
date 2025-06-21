package com.sprint.mission.discodeit.controller;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentProcessingException;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import com.sprint.mission.discodeit.vo.BinaryContentData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> create(
      @RequestPart("messageCreateRequest") @Valid MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    List<BinaryContentData> binaryContentDataList = resolveAttachmentRequest(attachments);
    CreateMessageCommand command = toCreateCommand(request, binaryContentDataList);
    MessageResponse message = messageService.create(command);

    return ResponseEntity.created(URI.create("/api/messages/" + message.id()))
        .body(message);
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageResponse>> findAllByChannelId(
      @RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor,
      @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    return ResponseEntity.ok(
        messageService.findAllByChannelIdWithCursor(channelId, cursor, pageable));
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageResponse> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    return ResponseEntity.ok(messageService.updateContent(messageId, request.newContent()));
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  private List<BinaryContentData> resolveAttachmentRequest(List<MultipartFile> attachments) {
    if (attachments == null || attachments.isEmpty()) {
      return List.of();
    }

    return attachments.stream()
        .filter(mf -> !mf.isEmpty())
        .map(this::toBinaryContentData)
        .toList();
  }

  private BinaryContentData toBinaryContentData(MultipartFile file) {
    try {
      return new BinaryContentData(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getBytes());
    } catch (IOException e) {
      throw new BinaryContentProcessingException();
    }
  }

  private CreateMessageCommand toCreateCommand(MessageCreateRequest request,
      List<BinaryContentData> attachments) {
    return new CreateMessageCommand(
        request.content(),
        request.authorId(),
        request.channelId(),
        attachments);
  }
}
