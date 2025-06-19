package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
@RequestMapping("/api/messages")
@RestController
public class MessageController implements MessageApi {

    private final MessageService messageService;
    private final PageResponseMapper pageResponseMapper;

    // 신규 메시지 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
        @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

        List<BinaryContentCreateRequest> binaryContents = resolveAttachmentRequest(attachments);
        MessageDto message = messageService.create(messageCreateRequest, binaryContents);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(message);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private List<BinaryContentCreateRequest> resolveAttachmentRequest(
        List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }

        return attachments.stream()
            .filter(file -> !file.isEmpty())
            .map(file -> {
                try {
                    return new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                    );
                } catch (IOException e) {
                    throw new RuntimeException("파일 처리 중 오류 발생", e);
                }
            })
            .toList();
    }

    // 메시지 수정
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(
        @PathVariable UUID messageId,
        @Valid @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        MessageDto updatedMessage = messageService.update(messageId, messageUpdateRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedMessage);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> delete(
        @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    // 특정 채널 메시지 목록 조회
    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
        @RequestParam("channelId") UUID channelId,
        @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}


