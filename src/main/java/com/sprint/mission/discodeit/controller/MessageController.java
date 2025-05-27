package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
    name = "메시지 Swagger 테스트 컨트롤러",
    description = "메시지에 관한 메서드를 제어하는 Swagger 테스트 컨트롤러입니다."
)
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessageController {

    private final MessageService messageService;

    // 신규 메시지 생성
    @Operation(summary = "Message 생성")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
            content = @Content(mediaType = "*/*", schema = @Schema(implementation = Message.class))),
        @ApiResponse(responseCode = "404", description = "User 또는 Channel을 찾을 수 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "User or Channel not found")))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
        @Parameter(description = "Message 생성 정보")
        @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
        @Parameter(description = "Message 첨부 파일들")
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

        List<BinaryContentCreateRequest> binaryContents = resolveAttachmentRequest(attachments);
        Message message = messageService.create(messageCreateRequest, binaryContents);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
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
    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message 내용이 성공적으로 수정됨",
            content = @Content(mediaType = "*/*", schema = @Schema(implementation = Message.class))),
        @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "Message not found")))
    })
    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> update(
        @Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        Message updatedMessage = messageService.update(messageId, messageUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
    }

    // 메시지 삭제
    @Operation(summary = "Message 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "Message not found")))
    })
    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> delete(
        @Parameter(name = "messageId", description = "삭제할 Message ID", required = true) @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 채널 메시지 목록 조회
    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
            content = @Content(mediaType = "*/*")),
        @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "Channel not found")))
    })
    @GetMapping
    public ResponseEntity<List<Message>> findAllByChannelId(
        @Parameter(name = "channelId", description = "조회할 Channel ID", required = true) UUID channelId
    ) {
        List<Message> messageList = messageService.findAllByChannelId(channelId);

        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }

}


