package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.binaryContent.InvalidFileProcessingException;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
        @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        log.info("메시지 생성 요청 - channelId: {}, userId: {}, 첨부파일 수: {}",
            messageCreateRequest.channelId(), messageCreateRequest.authorId(),
            attachments != null ? attachments.size() : 0);

        List<BinaryContentCreateRequest> attachmentRequests = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile file : attachments) {
                resolveAttachmentRequest(file).ifPresent(attachmentRequests::add);
            }
        }

        MessageDto created = messageService.create(messageCreateRequest, attachmentRequests);

        log.info("메시지 생성 완료 - messageId: {}, channelId: {}, 첨부파일 수: {}",
            created.id(), created.channelId(), attachmentRequests.size());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    @PatchMapping(path = "{messageId}")
    public ResponseEntity<MessageDto> update(
        @PathVariable("messageId") UUID messageId,
        @RequestBody MessageUpdateRequest request
    ) {
        log.info("메시지 수정 요청 - messageId: {}", messageId);

        MessageDto updatedMessage = messageService.update(messageId, request);

        log.info("메시지 수정 완료 - messageId: {}", messageId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedMessage);
    }

    @DeleteMapping(path = "{messageId}")
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        log.info("메시지 삭제 요청 - messageId: {}", messageId);

        messageService.delete(messageId);
        log.info("메시지 삭제 완료 - messageId: {}", messageId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
        @RequestParam("channelId") UUID channelId,
        @RequestParam(value = "cursor", required = false) Instant cursor,
        @PageableDefault(
            size = 50,
            page = 0,
            sort = "createdAt",
            direction = Direction.DESC
        ) Pageable pageable) {
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor,
            pageable);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messages);
    }

    private Optional<BinaryContentCreateRequest> resolveAttachmentRequest(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }
        try {
            log.debug("첨부 파일 처리 중 - 파일명: {}, 크기: {} bytes, 타입: {}",
                file.getOriginalFilename(), file.getSize(),
                file.getContentType());
            return Optional.of(new BinaryContentCreateRequest(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
            ));
        } catch (IOException e) {
            throw new InvalidFileProcessingException(file.getOriginalFilename());
        }
    }
}
