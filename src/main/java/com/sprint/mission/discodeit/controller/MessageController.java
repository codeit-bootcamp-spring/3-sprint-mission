package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes());
              } catch (IOException e) {
                throw new CustomException.BinaryContentNotFoundException("첨부파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
              }
            })
            .toList())
        .orElse(new ArrayList<>());
    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    MessageResponse response = new MessageResponse(
        createdMessage.id(),
        createdMessage.createdAt(),
        createdMessage.updatedAt(),
        createdMessage.content(),
        createdMessage.channelId(),
        createdMessage.author() != null ? createdMessage.author().id() : null,
        createdMessage.attachments().stream()
            .map(attachment -> attachment.id())
            .collect(Collectors.toList()));
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @PatchMapping(path = "{messageId}")
  public ResponseEntity<MessageResponse> update(@PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    MessageDto updatedMessage = messageService.update(messageId, request);
    MessageResponse response = new MessageResponse(
        updatedMessage.id(),
        updatedMessage.createdAt(),
        updatedMessage.updatedAt(),
        updatedMessage.content(),
        updatedMessage.channelId(),
        updatedMessage.author() != null ? updatedMessage.author().id() : null,
        updatedMessage.attachments().stream()
            .map(attachment -> attachment.id())
            .collect(Collectors.toList()));
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @DeleteMapping(path = "{messageId}")
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<List<MessageResponse>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId) {
    List<MessageDto> messages = messageService.findAllByChannelId(channelId);
    List<MessageResponse> responses = messages.stream()
        .map(message -> new MessageResponse(
            message.id(),
            message.createdAt(),
            message.updatedAt(),
            message.content(),
            message.channelId(),
            message.author() != null ? message.author().id() : null,
            message.attachments().stream()
                .map(attachment -> attachment.id())
                .collect(Collectors.toList())))
        .toList();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(responses);
  }
}
