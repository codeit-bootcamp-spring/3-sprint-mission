package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
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
@RestController
@RequestMapping("/api/messages")
@Slf4j
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> create(
        @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        log.info("[MessageController] Create message request received. [channelId={}]",
            messageCreateRequest.channelId());

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
                        log.error("[MessageController] Failed to read attachment file.", e);
                        throw new RuntimeException(e);
                    }
                })
                .toList())
            .orElse(new ArrayList<>());

        MessageResponse createdMessage = messageService.create(messageCreateRequest,
            attachmentRequests);

        log.debug("[MessageController] Message created. [id={}]", createdMessage.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @PatchMapping(path = "{messageId}")
    public ResponseEntity<MessageResponse> update(
        @PathVariable("messageId") UUID messageId,
        @Valid @RequestBody MessageUpdateRequest request
    ) {
        log.info("[MessageController] Update message request received. [id={}]", messageId);

        MessageResponse updatedMessage = messageService.update(messageId, request);

        log.debug("[MessageController] Message updated. [id={}]", updatedMessage.id());
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping(path = "{messageId}")
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        log.info("[MessageController] Delete message request received. [id={}]", messageId);

        try {
            messageService.delete(messageId);
            log.debug("[MessageController] Message deleted. [id={}]", messageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("[MessageController] Error occurred while deleting message. [id={}]",
                messageId, e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageResponse>> findAllByChannelId(
        @RequestParam("channelId") UUID channelId,
        @RequestParam(value = "cursor", required = false) Instant cursor,
        @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        log.info("[MessageController] Fetch messages request received. [channelId={}] [cursor={}]",
            channelId, cursor);

        PageResponse<MessageResponse> messages = messageService.findAllByChannelId(channelId,
            cursor, pageable);

        log.debug("[MessageController] Messages retrieved. [count={}] [channelId={}]",
            messages.content().size(), channelId);
        return ResponseEntity.ok(messages);
    }
}
