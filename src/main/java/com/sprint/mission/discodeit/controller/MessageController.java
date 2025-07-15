package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.PageResponse;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : MessageController
 * author         : doungukkim
 * date           : 2025. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 11.        doungukkim       최초 생성
 */
@RestController
@RequestMapping("api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {
    private final MessageService messageService;


    @GetMapping
    public ResponseEntity<PageResponse> findMessagesInChannel(
        @RequestParam UUID channelId,
        @RequestParam(required = false) Instant cursor,
        Pageable pageable) {
        return ResponseEntity.ok(messageService.findAllByChannelIdAndCursor(channelId, cursor, pageable));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> creatMessage(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachmentFiles
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.createMessage(request, attachmentFiles));
    }

    @DeleteMapping(path = "/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable @NotNull UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request) {
        return ResponseEntity.ok(messageService.updateMessage(messageId, request));
    }
}