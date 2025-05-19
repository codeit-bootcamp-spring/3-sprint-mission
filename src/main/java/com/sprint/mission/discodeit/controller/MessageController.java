package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Message> create(
            @RequestPart("request") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> files,
            HttpMethod httpMethod) throws IOException {
        List<BinaryContentCreateRequest> attachments = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                attachments.add(new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                ));
            }
        }
        return ResponseEntity.ok(messageService.create(request, attachments));
    }


    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<Message> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        return ResponseEntity.ok(messageService.update(messageId, request));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}