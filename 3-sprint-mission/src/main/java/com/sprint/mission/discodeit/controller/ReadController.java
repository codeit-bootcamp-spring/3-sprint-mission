package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ReadStatusService;
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

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@RestController
@Tag(
        name = "ReadStatus"
        , description = "Message 읽음 상태 API"
)
public class ReadController {
    private final ReadStatusService readStatusService;

    @Operation(
            summary = "Message 읽음 상태 생성"
            , operationId = "create_1"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ReadStatus.class)))
                    , @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함", content = @Content(schema = @Schema(example = "ReadStatus with userId {userId} and channelId {channelId} already exists")))
                    , @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음", content = @Content(schema = @Schema(example = "Channel | User with id {channelId | userId} not found")))
            }
    )
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatus readStatus = readStatusService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatus);
    }

    @Operation(
            summary = "Message 읽음 상태 수정"
            , operationId = "update_1"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = ReadStatus.class)))
                    , @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음", content = @Content(schema = @Schema(example = "ReadStatus with id {readStatusId} not found")))
            }
    )
    @Parameter(
            name = "readStatusId"
            , description = "수정할 읽음 상태 ID"
            , in = ParameterIn.PATH
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @PatchMapping(
            value = "/{readStatusId}"
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateDTO
    ) {
        ReadStatus readStatusUpdate = readStatusService.update(readStatusId, readStatusUpdateDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatusUpdate);
    }

    @Operation(
            summary = "User의 Message 읽음 상태 목록 조회"
            , operationId = "findAllByUserId"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 조회 성공"
                            , content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))))
            }
    )
    @Parameter(
            name = "userId"
            , in = ParameterIn.QUERY
            , description = "조회할 User ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
//        List<ReadStatus> readStatus = readStatusService.findAll();
        List<ReadStatus> readStatus = readStatusService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatus);
    }
}
