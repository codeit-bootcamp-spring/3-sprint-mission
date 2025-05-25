package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Controller
public class MessageController {

    private final MessageService messageService;
    private final BinaryContentService binaryContentService;

    // POST /api/messages - Message 생성
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MessageDto> sendMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        try {
            List<UUID> attachmentIds = new ArrayList<>();
            if (attachments != null && !attachments.isEmpty()) {
                List<BinaryContentCreateRequest> attachmentRequests = attachments.stream()
                        .map(this::createBinaryContentCreateRequest)
                        .collect(Collectors.toList());

                List<BinaryContent> savedAttachments = binaryContentService.saveAll(attachmentRequests);
                attachmentIds = savedAttachments.stream()
                        .map(BinaryContent::getId)
                        .collect(Collectors.toList());
            }

            MessageCreateRequest requestWithAttachments = new MessageCreateRequest(
                    request.content(),
                    request.channelId(),
                    request.authorId(),
                    attachmentIds);

            Message createdMessage = messageService.createMessage(requestWithAttachments);

            return ResponseEntity.status(HttpStatus.CREATED).body(convertToMessageDto(createdMessage));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // GET /api/messages - Channel의 Message 목록 조회
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<MessageDto>> getChannelMessages(@RequestParam("channelId") UUID channelId) {
        try {
            List<Message> messages = messageService.findAllByChannelId(channelId);
            List<MessageDto> messageDtos = messages.stream()
                    .map(this::convertToMessageDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(messageDtos);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // PATCH /api/messages/{messageId} - Message 내용 수정
    @RequestMapping(path = "/{messageId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request) {
        try {
            Message updatedMessage = messageService.updateMessage(messageId, request);
            return ResponseEntity.ok(convertToMessageDto(updatedMessage));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // DELETE /api/messages/{messageId} - Message 삭제
    @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteMessage(@PathVariable("messageId") UUID messageId) {
        try {
            messageService.deleteMessage(messageId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message with id " + messageId + " not found");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete message: " + e.getMessage());
        }
    }

    // Message 엔티티를 MessageDto로 변환하는 헬퍼 메소드
    private MessageDto convertToMessageDto(Message message) {
        return new MessageDto(
                message.getMessageId(),
                message.getContent(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getAttachmentIds());
    }

    // MultipartFile에서 BinaryContentCreateRequest 객체 생성
    private BinaryContentCreateRequest createBinaryContentCreateRequest(MultipartFile file) {
        try {
            return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create BinaryContentCreateRequest from MultipartFile", e);
        }
    }
}