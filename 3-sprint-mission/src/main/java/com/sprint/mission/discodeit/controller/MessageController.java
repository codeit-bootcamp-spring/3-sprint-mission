package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
@Tag(
        name = "Message"
        , description = "Message API"
)
public class MessageController {
    private final MessageService messageService;

    @Operation(
            summary = "Message 생성"
            , operationId = "create_2"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Message.class)))
                    , @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(schema = @Schema(example = "Channel | Author with id {channelId | authorId} not found")))
            }
    )

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart(value = "messageCreateRequest") MessageCreateRequest messageCreateDTO,
            @Parameter(
                    name = "attachments"
                    , description = "Message 첨부 파일들"
                    , content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(type="array", format="binary")
                            )
                    )
            )
            @RequestPart(value = "attachments", required = false) List<MultipartFile> files
    ) {

        List<BinaryContentCreateRequest> attachments =
                resolveBinaryContentRequest(files);

        Message createdMessage = messageService.create(messageCreateDTO, attachments);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private List<BinaryContentCreateRequest> resolveBinaryContentRequest(List<MultipartFile> files) {

        List<BinaryContentCreateRequest> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    BinaryContentCreateRequest dto = new BinaryContentCreateRequest(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                    );

                    attachments.add(dto);
                } catch (IOException e) {
                    throw new RuntimeException("파일 처리 중 오류 발생: " + file.getOriginalFilename(), e);
                }
            }
        }

        return attachments;

    }

    // 메시지 다건 조회
    @Operation(
            summary = "Channel의 Message 목록 조회"
            , operationId = "findAllByChannelId"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공"
                            , content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class))))
            }
    )
    @Parameter(
            name = "channelId"
            , in = ParameterIn.QUERY
            , description = "조회할 Channel ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Message>> findAll(
            @RequestParam UUID channelId
    ) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    // 메시지 수정
    @Operation(
            summary = "Message 내용 수정"
            , operationId = "update_2"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = Message.class)))
                    , @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(schema = @Schema(example = "Message with id {messageId} not found")))
            }
    )
    @Parameter(
            name = "messageId"
            , in = ParameterIn.PATH
            , description = "수정할 Message ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @PatchMapping(
            value = "/{messageId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> update(
            @PathVariable("messageId") UUID messageId
            , @RequestBody MessageUpdateRequest messageUpdateDTO
    ) {

        Message createdMessage = messageService.update(messageId, messageUpdateDTO);

        return ResponseEntity.ok(createdMessage);
    }

    // 메시지 삭제
    @Operation(
            summary = "Message 삭제"
            , operationId = "delete_1"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨")
                    , @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(schema = @Schema(example = "Message with id {messageId} not found")))
            }
    )
    @Parameter(
            name = "messageId"
            , in = ParameterIn.PATH
            , description = "삭제할 Message ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @DeleteMapping(
            value = "/{messageId}"
    )
    public ResponseEntity<String> delete(
            @PathVariable("messageId") UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity.ok("메시지를 삭제했습니다.");
    }
}