package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageAPI;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/messages")
@RestController
@Slf4j
public class MessageController implements MessageAPI {

  private final MessageService messageService;

  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart(value = "messageCreateRequest") @Valid MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    log.info("메시지 생성 요청 request={}", messageCreateRequest);

    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
                );
              } catch (IOException e) {
                log.error("파일 업로드 실패 {}", e.getMessage(), e);
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());
    log.info("메시지 첨부 파일 생성 요청 attachmentRequests={}", attachmentRequests);

    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    log.info("메시지 생성 완료 createdMessageId={}", createdMessage.id());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  // 메시지 다건 조회
  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam UUID channelId,
      @RequestParam(value = "cursor", required = false) Instant cursor,
      @PageableDefault(
          size = 50,
          page = 0,
          sort = "createdAt",
          direction = Direction.DESC
      ) Pageable pageable
  ) {
    PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }

  // 메시지 수정
  @PatchMapping(
      value = "/{messageId}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<MessageDto> update(
      @PathVariable("messageId") UUID messageId
      , @RequestBody @Valid MessageUpdateRequest messageUpdateDto
  ) {
    log.info("메시지 수정 요청 messageId={}, request={}", messageId, messageUpdateDto);

    MessageDto createdMessage = messageService.update(messageId, messageUpdateDto);
    log.info("메시지 수정 완료 messageId={}", messageId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(createdMessage);
  }

  // 메시지 삭제
  @DeleteMapping(
      value = "/{messageId}"
  )
  public ResponseEntity<String> delete(
      @PathVariable("messageId") UUID messageId
  ) {
    log.info("메시지 삭제 요청 messageId={}", messageId);

    messageService.delete(messageId);
    log.info("메시지 삭제 완료 messageId={}", messageId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body("메시지를 삭제했습니다.");
  }
}