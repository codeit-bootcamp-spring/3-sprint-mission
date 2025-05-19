package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@RestController
public class ReadStatusController {

    private final ReadStatusService readStatusService;
    private final UserService userService;

    /**
     * 특정 채널의 메시지 수신 정보 생성
     *
     * @param readStatusCreateRequest 메시지 수신 정보 생성 요청 DTO
     * @return 생성된 메시지 수신 정보 (HTTP 201 CREATED)
     */
    @Operation(
            summary = "Message 읽음 상태 생성",
            operationId = "create_1"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ReadStatus.class))),
                    @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(examples = @ExampleObject("Channel | User with id {channelId | userId} not found"))),
                    @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함", content = @Content(examples = @ExampleObject("ReadStatus with userId {userId} and channelId {channelId} already exists"))),
            }
    )
    @PostMapping
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest readStatusCreateRequest
    ) {
        ReadStatus createdReadStatus = readStatusService.create(readStatusCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReadStatus);
    }

    /**
     * 특정 채널의 메시지 수신 정보 수정
     *
     * @param readStatusId            조회할 메시지 수신 정보 ID
     * @param readStatusUpdateRequest 메시지 수신 정보 수정 요청 DTO
     * @return 수정된 메시지 수신 정보 (HTTP 200 OK)
     */
    @Operation(
            summary = "Message 읽음 상태 수정",
            operationId = "update_1"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = ReadStatus.class))),
                    @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음", content = @Content(examples = @ExampleObject("ReadStatus with id {readStatusId} not found")))
            }
    )
    @PatchMapping(path = "/{readStatusId}")
    public ResponseEntity<ReadStatus> update(
            @Parameter(description = "수정할 읽음 상태 ID", required = true)
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatus updatedReadStatus = readStatusService.update(readStatusId,
                readStatusUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedReadStatus);
    }

    /**
     * 특정 사용자의 메시지 수신 정보 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 메시지 수신 정보 목록 (HTTP 200 OK)
     */
    @Operation(
            summary = "User의 Message 읽음 상태 목록 조회",
            operationId = "findAllByUserId"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @Parameter(description = "조회할 User ID", required = true)
            @RequestParam("userId") UUID userId
    ) {
        // 유저 유효성 검사
        userService.find(userId);

        List<ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatusList);
    }
}
