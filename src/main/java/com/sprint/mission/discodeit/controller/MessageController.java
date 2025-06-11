package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
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
    public ResponseEntity<MessageDto> create(
        @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<MultipartFile> multipartAttachments = attachments != null ? attachments : List.of();

        List<BinaryContentCreateRequest> attachmentRequests = multipartAttachments.stream()
            .map(this::resolveAttachmentRequest)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

        MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdMessage);
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
        @RequestParam UUID channelId,
        @RequestParam(required = false) Instant cursor,
        @ParameterObject Pageable pageable
    ) {
        PageResponse<MessageDto> pageResponse = messageService.findAllByChannelId(channelId,
            cursor, pageable);
        return ResponseEntity.ok(pageResponse);
    }

    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<MessageDto> update(
        @PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        MessageDto updatedMessage = messageService.update(messageId, messageUpdateRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedMessage);
    }

    @DeleteMapping(path = "/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);

        return ResponseEntity.noContent().build();
    }

    private Optional<BinaryContentCreateRequest> resolveAttachmentRequest(
        MultipartFile attachment) {
        return Optional.ofNullable(attachment)
            .filter(file -> !file.isEmpty())
            .map(file -> {
                try {
                    return new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                    );
                } catch (IOException e) {
                    throw new RuntimeException("파일 읽기 중 오류 발생", e);
                }
            });
    }

}

