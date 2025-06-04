package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.BinaryContentUtil;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    /**
     * 메시지 전송
     *
     * @param messageCreateRequest 메시지 생성 요청 DTO
     * @param attachments          첨부파일 목록
     * @return 생성된 Message (HTTP 201 CREATED)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<MessageDto> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequest> attachmentsRequestDTO =
                Optional.ofNullable(attachments)
                        .orElse(List.of())
                        .stream()
                        .map(BinaryContentUtil::resolveFile)
                        .flatMap(Optional::stream)
                        .toList();

        MessageDto createdMessage = messageService.create(messageCreateRequest,
                attachmentsRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessage);
    }

    /**
     * 메시지 수정
     *
     * @param messageId            수정할 메시지 ID
     * @param messageUpdateRequest 메시지 수정 요청 DTO
     * @return 수정된 Message (HTTP 200 OK)
     */
    @PatchMapping(path = "/{messageId}")
    @Override
    public ResponseEntity<MessageDto> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        MessageDto updatedMessage = messageService.update(messageId, messageUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedMessage);
    }

    /**
     * 메시지 삭제
     *
     * @param messageId 삭제할 메시지 ID
     * @return 삭제 완료 메시지 (HTTP 200 OK)
     */
    @DeleteMapping(path = "/{messageId}")
    @Override
    public ResponseEntity<Void> delete(
            @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * 특정 채널의 메시지 목록 조회
     *
     * @param channelId 채널 ID
     * @return 조회된 메시지 목록 (HTTP 200 OK)
     */
    @GetMapping
    @Override
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId,
            @RequestParam(required = false) Instant cursor,
            Pageable pageable
    ) {
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId,
                cursor, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messages);
    }
}
