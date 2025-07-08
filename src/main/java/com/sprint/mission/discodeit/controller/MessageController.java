package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
@Slf4j
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDto> create(
        @RequestPart("messageCreateRequest") MessageCreateRequest request,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        log.info("메세지 생성 요청 : request = {}, attachments = {}",
            request, attachments != null ? attachments.size() : 0);
        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
            .map(files -> files.stream()
                .map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList())
            .orElse(new ArrayList<>());
        MessageDto createdMessage = messageService.create(request, attachmentRequests);
        log.debug("메세지 생성 응답 : {}", createdMessage);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdMessage);
    }


    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(
        @PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest request
    ) {
        log.info("메세지 수정 요청 : id = {}, request = {}", messageId, request);
        MessageDto updateMessage = messageService.update(messageId, request);
        log.debug("메세지 수정 응답 : {}", updateMessage);

        return ResponseEntity.status(HttpStatus.OK)
            .body(updateMessage);
    }


    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID messageId
    ) {
        log.info("메세지 삭제 요청 : id = {}", messageId);
        messageService.delete(messageId);
        log.debug("메세지 삭제 완료");

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }


    // 특정 채널의 소속된 메세지 목록 조회( GET )
    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
        @RequestParam("channelId") UUID channelId,
        @RequestParam(value = "cursor", required = false) Instant cursor,
        @PageableDefault(
            size = 50,
            page = 0,
            sort = "createdAt",
            direction = Direction.DESC
        ) Pageable pageable) {
        log.info("채널별 메세지 목록 조회 요청 : channelId = {}, cursor = {}, pageable = {}",
            channelId, cursor, pageable);
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor,
            pageable);
        log.debug("채널별 메세지 목록 조회 응답 : totalElements = {}", messages.totalElements());

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messages);
    }
}
