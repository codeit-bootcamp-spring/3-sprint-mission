package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

    // 특정 채널 메시지 수신 정보 생성
    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = ReadStatus.class))),
        @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
            content = @Content(examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists"))),
        @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found")))
    })
    ResponseEntity<ReadStatusDto> create(
        @Parameter(description = "Message 읽음 상태 생성 정보") ReadStatusCreateRequest readStatusCreateRequest
    );

    // 특정 채널 메시지 수신 정보 수정
    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
            content = @Content(schema = @Schema(implementation = ReadStatus.class))),
        @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")))
    })
    ResponseEntity<ReadStatusDto> update(
        @Parameter(description = "수정할 읽음 상태 ID") UUID readStatusId,
        @Parameter(description = "수정할 읽음 상태 정보") ReadStatusUpdateRequest readStatusUpdateRequest
    );

    // 특정 사용자의 메시지 수신 정보 조회
    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))))
    })
    ResponseEntity<List<ReadStatusDto>> findAllByUserId(
        @Parameter(name = "userId", description = "조회할 User ID") UUID userId
    );
}
