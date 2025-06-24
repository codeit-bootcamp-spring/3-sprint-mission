package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "ReadStatus",
        description = "ReadStatus API"
)
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @Operation(
            summary = "Message 읽음 상태 생성",
            operationId = "create_1",
            tags = {"ReadStatus"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ReadStatus.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 읽음 상태가 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists")
                    )
            )
    })
    @Parameter(
            name = "readStatusCreateRequestDto",
            description = "생성할 읽음 상태 정보",
            required = true,
            schema = @Schema(implementation = ReadStatusCreateRequest.class)
    )
    @PostMapping("")
    public ResponseEntity<ReadStatusDto> create(@Valid @RequestBody ReadStatusCreateRequest request) {

        ReadStatusDto createdReadStatus = readStatusService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReadStatus);
    }

    @Operation(
            summary = "Message 읽음 상태 수정",
            operationId = "update_1"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ReadStatus.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")
                    )
            )
    })
    @Parameter(
            name = "readStatusId",
            description = "수정할 읽음 상태 ID",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string", format = "uuid")
    )
    @Parameter(
            name = "readStatusUpdateRequest",
            description = "수정할 읽음 상태 정보",
            required = true,
            schema = @Schema(implementation = ReadStatusUpdateRequest.class)
    )
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(@PathVariable("readStatusId") UUID readStatusId,
                                                @RequestBody ReadStatusUpdateRequest request) {

        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedReadStatus);
    }

    @Operation(
            summary = "User의 Message 읽음 상태 목록 조회",
            operationId = "findAllByUserId"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Message 읽음 상태 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ReadStatus.class)
                    )
            )
    )
    @Parameter(
            name = "userId",
            description = "조회할 User ID",
            required = true,
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "uuid")
    )
    @GetMapping("")
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {

        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatuses);
    }
}
