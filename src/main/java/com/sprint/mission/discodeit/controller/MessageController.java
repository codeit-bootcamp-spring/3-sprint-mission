package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessageController implements MessageApi {
    private final MessageService messageService;

    // 메시지 송신
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequest> binaryRequests = Optional.ofNullable(attachments)
                .orElse(Collections.emptyList())
                .stream()
                .map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                                file.getOriginalFilename(),
                                file.getBytes(),
                                file.getContentType()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("파일 처리 중 오류", e);
                    }
                })
                .collect(Collectors.toList());

        Message message = messageService.create(messageCreateRequest, binaryRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    // 메시지 수정
    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        Message updated = messageService.update(messageId, request);
        return ResponseEntity.ok(updated);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    // 특정 채널 메시지 목록 조회
    @GetMapping
    public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }
}
