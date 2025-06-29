package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

    // 신규 메시지 생성
    @Operation(summary = "Message 생성")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = MessageDto.class))),
        @ApiResponse(responseCode = "404", description = "User 또는 Channel을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "User or Channel with id {authorId | channelId} not found")))
    })
    ResponseEntity<MessageDto> create(
        @Parameter(description = "Message 생성 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
        @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
        @Parameter(description = "Message 첨부 파일들", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments);


    // 메시지 수정
    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message 내용이 성공적으로 수정됨",
            content = @Content(mediaType = "*/*", schema = @Schema(implementation = MessageDto.class))),
        @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "Message with id {messageId} not found")))
    })
    ResponseEntity<MessageDto> update(
        @Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest messageUpdateRequest);

    // 메시지 삭제
    @Operation(summary = "Message 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨", content = @Content),
        @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
    })
    ResponseEntity<Void> delete(
        @Parameter(name = "messageId", description = "삭제할 Message ID", required = true) @PathVariable UUID messageId);

    // 특정 채널 메시지 목록 조회
    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = MessageDto.class))),
        @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "Channel not found")))
    })
    ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
        @Parameter(description = "조회할 Channel ID", name = "channelId", required = true) UUID channelId,
        @Parameter(description = "페이징 커서 정보 (createdAt)") Instant cursor,
        @Parameter(description = "페이징 정보", example = "{\"size\": 50, \"sort\": \"createdAt,desc\"}") Pageable pageable
    );
}
