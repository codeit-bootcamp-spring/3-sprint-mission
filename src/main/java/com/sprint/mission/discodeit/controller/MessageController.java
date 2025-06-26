package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
        @RequestPart("messageCreateRequest") @Valid MessageCreateRequest messageCreateRequest,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<MultipartFile> multipartAttachments = attachments != null ? attachments : List.of();

        List<BinaryContentCreateRequest> attachmentRequests =
            Optional.ofNullable(attachments)
                .orElse(List.of())
                .stream()
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

    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<MessageDto> update(
        @PathVariable("messageId") UUID messageId,
        @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        MessageDto updatedMessage = messageService.update(messageId, messageUpdateRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedMessage);
    }

    @DeleteMapping(path = "/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
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

