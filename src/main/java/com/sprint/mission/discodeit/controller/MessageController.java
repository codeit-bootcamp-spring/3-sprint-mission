package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.AdvancedJpaPageResponse;
import com.sprint.mission.discodeit.dto.message.response.JpaMessageResponse;
import com.sprint.mission.discodeit.unit.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<AdvancedJpaPageResponse> findMessagesInChannel(
        @RequestParam UUID channelId,
        @RequestParam(required = false) Instant cursor,
        Pageable pageable) {
        AdvancedJpaPageResponse response = messageService.findAllByChannelIdAndCursor(channelId, cursor, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> creatMessage(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachmentFiles
    ) {
        JpaMessageResponse message = messageService.createMessage(request, attachmentFiles);
        return ResponseEntity.status(201).body(message);
    }

    @DeleteMapping(path = "/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable @NotNull UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<JpaMessageResponse> updateMessage(
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request) {
        JpaMessageResponse response = messageService.updateMessage(messageId, request);
        return ResponseEntity.status(200).body(response);
    }
}