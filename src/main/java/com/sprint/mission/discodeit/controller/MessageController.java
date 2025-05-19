package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
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

@Tag(name = "Message", description = "Message API")
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessageController {

    private final MessageService messageService;
    private final ChannelService channelService;

    /**
     * 메시지 전송
     *
     * @param messageCreateRequest 메시지 생성 요청 DTO
     * @param attachments          첨부파일 목록
     * @return 생성된 Message (HTTP 201 CREATED)
     */
    @Operation(
            summary = "Message 생성",
            operationId = "create_2"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Message.class))),
                    @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(examples = @ExampleObject("Channel | Author with id {channelId | authorId} not found")))
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @Parameter(description = "Message 첨부 파일들", required = false)
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        // 첨부파일 생성 요청 DTO 목록 설정
        List<BinaryContentCreateRequest> attachmentsRequestDTO =
                Optional.ofNullable(attachments)
                        .orElse(List.of()).stream()
                        .map(this::resolveAttachmentRequest)
                        .flatMap(Optional::stream)
                        .toList();

        Message createdMessage = messageService.create(messageCreateRequest,
                attachmentsRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessage);
    }

    /**
     * MultipartFile 타입의 요청값을 BinaryContentCreateReqeust 타입으로 변환
     *
     * @param attachment 첨부파일 (MultipartFile)
     * @return 생성된 바이너리 파일 생성 요청 DTO
     */
    private Optional<BinaryContentCreateRequest> resolveAttachmentRequest(
            MultipartFile attachment) {
        if (attachment.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        attachment.getOriginalFilename(),
                        attachment.getContentType(),
                        attachment.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 메시지 수정
     *
     * @param messageId            수정할 메시지 ID
     * @param messageUpdateRequest 메시지 수정 요청 DTO
     * @return 수정된 Message (HTTP 200 OK)
     */
    @Operation(
            summary = "Message 내용 수정",
            operationId = "update_2"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = Message.class))),
                    @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = @ExampleObject("Message with id {messageId} not found")))
            }
    )
    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<Message> update(
            @Parameter(description = "수정할 Message ID", required = true)
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        Message updatedMessage = messageService.update(messageId, messageUpdateRequest);

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
    @Operation(
            summary = "Message 삭제",
            operationId = "delete_1"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
                    @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = @ExampleObject("Message with id {messageId} not found")))
            }
    )
    @DeleteMapping(path = "/{messageId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Message ID", required = true)
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
    @Operation(
            summary = "Channel의 Message 목록 조회",
            operationId = "findAllByChannelId"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<Message>> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID", required = true)
            @RequestParam("channelId") UUID channelId
    ) {
        // 채널 유효성 검사
        channelService.find(channelId);

        List<Message> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messages);
    }
}
