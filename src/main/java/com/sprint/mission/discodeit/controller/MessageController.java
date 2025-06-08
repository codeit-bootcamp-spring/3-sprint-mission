package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.common.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
  public ResponseEntity<MessageDto> create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
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
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @PatchMapping(path = "{messageId}")
  public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request) {
    MessageDto updatedMessage = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @DeleteMapping(path = "{messageId}")
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @PageableDefault(size = Constants.Pagination.DEFAULT_PAGE_SIZE, sort = Constants.Pagination.DEFAULT_SORT_FIELD) Pageable pageable) {
    PageResponse<MessageDto> messages = messageService.findAllByChannelIdWithPaging(channelId, pageable);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}
