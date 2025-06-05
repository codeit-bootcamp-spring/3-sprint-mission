package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "ReadStatus API")
public interface ReadStatusApi {

    @Operation(summary = "ReadStatus 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "ReadStatus가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ReadStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "User 또는 Channel을 찾을 수 없음", content = @Content(examples = @ExampleObject("User with id {userId} does not exist"))),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 ReadStatus", content = @Content(examples = @ExampleObject("ReadStatus with userId {userId} and channelId {channelId} already exists")))
    })
    ResponseEntity<ReadStatusResponse> create(
            @Parameter(description = "ReadStatus 생성 정보") ReadStatusCreateRequest request);

    @Operation(summary = "ReadStatus 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ReadStatus가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = ReadStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "ReadStatus를 찾을 수 없음", content = @Content(examples = @ExampleObject("ReadStatus with id {readStatusId} not found")))
    })
    ResponseEntity<ReadStatusResponse> update(
            @Parameter(description = "수정할 ReadStatus ID") UUID readStatusId,
            @Parameter(description = "수정할 ReadStatus 정보") ReadStatusUpdateRequest request);

    @Operation(summary = "User의 모든 ReadStatus 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ReadStatus 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusResponse.class))))
    })
    ResponseEntity<List<ReadStatusResponse>> findAllByUserId(
            @Parameter(description = "User ID") UUID userId);
}