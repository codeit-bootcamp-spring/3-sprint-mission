package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.request.MessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.global.response.CustomApiResponse;
import com.sprint.mission.discodeit.service.MessageService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<CustomApiResponse<MessageResponse>> create(
      @RequestPart("messageCreateRequest") MessageRequest.Create messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(CustomApiResponse.created(messageService.create(messageCreateRequest, attachments)));
  }

  @PatchMapping(path = "{messageId}")
  @Override
  public ResponseEntity<CustomApiResponse<MessageResponse>> update(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageRequest.Update request) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CustomApiResponse.success(messageService.update(messageId, request)));
  }

  @DeleteMapping(path = "{messageId}")
  @Override
  public ResponseEntity<CustomApiResponse<Void>> delete(
      @PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CustomApiResponse.success("메시지 삭제 성공"));
  }

  @GetMapping
  @Override
  public ResponseEntity<CustomApiResponse<PageResponse<MessageResponse>>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CustomApiResponse.success(messageService.findAllByChannelId(channelId)));
  }
}
