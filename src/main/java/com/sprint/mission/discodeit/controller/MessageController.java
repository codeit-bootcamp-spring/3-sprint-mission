package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Messages")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "메시지 생성")
    @ApiResponse(responseCode = "201",  description = "생성 성공")
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody MessageCreateRequest req) {
        Message message = messageService.createMessage(req);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "단일 메시지 조회")
    @ApiResponse(responseCode = "200",  description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable UUID id) {
        Message message = messageService.getMessage(id);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "채널 별 메시지 조회")
    @ApiResponse(responseCode = "200",  description = "조회 성공")
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Message>> getMessagesByChannel(@PathVariable UUID channelId) {
        List<Message> messageList = messageService.getMessagesByChannel(channelId);
        return ResponseEntity.ok(messageList);
    }

    @Operation(summary = "전체 메시지 조회")
    @ApiResponse(responseCode = "200",  description = "조회 성공")
    @GetMapping("/all")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messageList = messageService.getAllMessages();
        return ResponseEntity.ok(messageList);
    }

    @Operation(summary = "메시지 수정")
    @ApiResponse(responseCode = "200",  description = "수정 성공")
    @PutMapping
    public ResponseEntity<Void> updateMessage(@RequestBody MessageUpdateRequest req) {
        messageService.updateMessage(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "메시지 삭제")
    @ApiResponse(responseCode = "204",  description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
