package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<MessageResponse> createMessage(
        @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
            .map(files -> files.stream()
                .map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                            file.getOriginalFilename(),
                            file.getBytes(),
                            file.getContentType()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList())
            .orElse(new ArrayList<>());
        Message newMessage = messageService.createMessage(messageCreateRequest, attachmentRequests);
        MessageResponse response = MessageResponse.fromEntity(newMessage);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @PatchMapping("/{messageId}")
    @Override
    public ResponseEntity<MessageResponse> updateMessage(@PathVariable("messageId") UUID messageId,
        @Valid @RequestBody MessageUpdateRequest request) {
        Message updatedMessage = messageService.updateMessage(messageId, request);
        MessageResponse response = MessageResponse.fromEntity(updatedMessage);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @DeleteMapping("/{messageId}")
    @Override
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId,
        @RequestParam("senderId") UUID senderId) {
        messageService.deleteMessage(messageId, senderId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping
    @Override
    public ResponseEntity<List<MessageResponse>> findAllMessageInChannel(
        @RequestParam("channelId") UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);

        List<MessageResponse> responses = messages.stream()
            .map(MessageResponse::fromEntity)
            .toList();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(responses);
    }
}