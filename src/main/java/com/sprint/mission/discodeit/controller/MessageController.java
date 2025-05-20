package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/channels/{channelId}/messages")
    public ResponseEntity<Message> create(@PathVariable UUID channelId,
                                          @RequestBody MessageCreateRequest request) {
        // channelId와 authorId는 request 안에 포함되어야 함
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.create(request, Collections.emptyList()));
    }

    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<List<Message>> findAllByChannel(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    @PutMapping("/messages/{id}")
    public ResponseEntity<Message> update(@PathVariable UUID id,
                                          @RequestBody MessageUpdateRequest request) {
        return ResponseEntity.ok(messageService.update(id, request));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
