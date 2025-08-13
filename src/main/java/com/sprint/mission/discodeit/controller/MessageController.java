package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 *
 * <p>메시지 생성, 수정, 삭제, 조회 기능을 제공하며, 이미지 첨부 기능을 지원합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessageController implements MessageApi {

    private final MessageService messageService;

    private static final String CONTROLLER_NAME = "[MessageController] ";

    /**
     * 새로운 메시지를 생성합니다.
     *
     * <p>텍스트 메시지와 함께 이미지 첨부가 가능합니다.</p>
     *
     * @param messageCreateRequest 메시지 생성 요청 정보
     * @param attachments 첨부할 이미지 파일 목록 (선택사항)
     * @return 생성된 메시지 DTO
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
        @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

        log.info(CONTROLLER_NAME + "메시지 생성 시도: channelId={}, authorId={}, content={}",
                messageCreateRequest.channelId(), messageCreateRequest.authorId(), messageCreateRequest.content());

        if (attachments != null && !attachments.isEmpty()) {
            log.info(CONTROLLER_NAME + "첨부파일 업로드됨: 개수={}", attachments.size());
            attachments.forEach(file -> 
                log.debug(CONTROLLER_NAME + "첨부파일 정보: filename={}, size={}, contentType={}",
                        file.getOriginalFilename(), file.getSize(), file.getContentType())
            );
        } else {
            log.info(CONTROLLER_NAME + "첨부파일 없음");
        }

        List<BinaryContentCreateRequest> binaryContents = resolveAttachmentRequest(attachments);
        MessageDto message = messageService.create(messageCreateRequest, binaryContents);

        log.info(CONTROLLER_NAME + "메시지 생성 성공: messageId={}, channelId={}",
                message.id(), message.channelId());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(message);
    }

    /**
     * MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환합니다.
     *
     * @param attachments 변환할 첨부파일 목록
     * @return BinaryContentCreateRequest 목록
     */
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
                    log.error(CONTROLLER_NAME + "파일 처리 중 오류 발생: filename={}", file.getOriginalFilename(), e);
                    throw new RuntimeException("파일 처리 중 오류 발생", e);
                }
            })
            .toList();
    }

    /**
     * 메시지를 수정합니다.
     *
     * @param messageId 수정할 메시지 ID
     * @param messageUpdateRequest 메시지 수정 요청 정보
     * @return 수정된 메시지 DTO
     */
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(
        @PathVariable UUID messageId,
        @Valid @RequestBody MessageUpdateRequest messageUpdateRequest) {

        log.info(CONTROLLER_NAME + "메시지 수정 시도: messageId={}, newContent={}",
                messageId, messageUpdateRequest.newContent());

        MessageDto updatedMessage = messageService.update(messageId, messageUpdateRequest);

        log.info(CONTROLLER_NAME + "메시지 수정 성공: messageId={}", messageId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedMessage);
    }

    /**
     * 메시지를 삭제합니다.
     *
     * @param messageId 삭제할 메시지 ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        log.info(CONTROLLER_NAME + "메시지 삭제 시도: messageId={}", messageId);

        messageService.delete(messageId);

        log.info(CONTROLLER_NAME + "메시지 삭제 성공: messageId={}", messageId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    /**
     * 특정 메시지를 조회합니다.
     *
     * @param messageId 조회할 메시지 ID
     * @return 조회된 메시지 DTO
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDto> find(@PathVariable UUID messageId) {
        log.info(CONTROLLER_NAME + "메시지 조회 시도: messageId={}", messageId);

        MessageDto message = messageService.find(messageId);

        log.info(CONTROLLER_NAME + "메시지 조회 성공: messageId={}, channelId={}",
                messageId, message.channelId());

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(message);
    }

    /**
     * 특정 채널의 메시지 목록을 페이지네이션으로 조회합니다.
     *
     * @param channelId 채널 ID
     * @param cursor 커서(실제로는 createdAt 값)
     * @param pageable 페이지네이션 정보
     * @return 페이지네이션된 메시지 목록
     */
    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
        @RequestParam("channelId") UUID channelId,
        @RequestParam(value = "cursor", required = false) Instant cursor, // createdAt 값
        @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        log.info(CONTROLLER_NAME + "채널 메시지 목록 조회 시도: channelId={}, cursor={}, page={}, size={}",
                channelId, cursor, pageable.getPageNumber(), pageable.getPageSize());

        PageResponse<MessageDto> pageResponse = messageService.findAllByChannelIdWithAuthor(
            channelId, cursor, pageable);

        log.info(CONTROLLER_NAME + "채널 메시지 목록 조회 성공: channelId={}, 조회된 메시지 수={}, 다음 커서={}",
                channelId, pageResponse.content().size(), pageResponse.nextCursor());

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(pageResponse);
    }
}


