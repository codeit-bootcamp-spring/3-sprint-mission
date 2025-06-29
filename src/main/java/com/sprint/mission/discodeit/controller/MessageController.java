package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadFailedException;
import com.sprint.mission.discodeit.common.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    int attachmentCount = Optional.ofNullable(attachments).map(List::size).orElse(0);
    log.info("메시지 생성 API 요청 - 채널 ID: {}, 작성자 ID: {}, 첨부파일 수: {}",
        messageCreateRequest.channelId(), messageCreateRequest.authorId(), attachmentCount);

    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                log.debug("첨부파일 처리 - 파일명: {}, 크기: {} bytes",
                    file.getOriginalFilename(), file.getSize());
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes());
              } catch (IOException e) {
                log.error("첨부파일 읽기 실패 - 파일명: {}, 오류: {}",
                    file.getOriginalFilename(), e.getMessage());
                throw FileUploadFailedException.withCause(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());

    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

    log.info("메시지 생성 API 응답 - 메시지 ID: {}, 채널 ID: {}, 작성자 ID: {}",
        createdMessage.id(), createdMessage.channelId(), createdMessage.author().id());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @PatchMapping(path = "{messageId}")
  public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request) {
    log.info("메시지 수정 API 요청 - 메시지 ID: {}", messageId);

    MessageDto updatedMessage = messageService.update(messageId, request);

    log.info("메시지 수정 API 응답 - 메시지 ID: {}, 작성자 ID: {}",
        updatedMessage.id(), updatedMessage.author().id());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @DeleteMapping(path = "{messageId}")
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    log.info("메시지 삭제 API 요청 - 메시지 ID: {}", messageId);

    messageService.delete(messageId);

    log.info("메시지 삭제 API 완료 - 메시지 ID: {}", messageId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @RequestParam(value = "cursor", required = false) String cursor,
      @PageableDefault(size = Constants.Pagination.DEFAULT_PAGE_SIZE, sort = Constants.Pagination.DEFAULT_SORT_FIELD) Pageable pageable) {

    log.debug("메시지 목록 API 요청 - 채널 ID: {}, 커서: {}, 페이지 크기: {}",
        channelId, cursor, pageable.getPageSize());

    PageResponse<MessageDto> messages = messageService.findAllByChannelIdWithCursorPaging(channelId, cursor,
        pageable);

    log.debug("메시지 목록 API 응답 - 채널 ID: {}, 메시지 수: {}, 다음 페이지 존재: {}",
        channelId, messages.content().size(), messages.hasNext());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}
