package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널 메시지 수신 정보 생성
    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
            content = @Content(mediaType = "*/*", schema = @Schema(implementation = ReadStatus.class))),
        @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists"))),
        @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found")))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadStatus> create(
        @Parameter(description = "Message 읽음 상태 생성 정보") @RequestBody ReadStatusCreateRequest readStatusCreateRequest
    ) {
        ReadStatus readStatus = readStatusService.create(readStatusCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    // 특정 채널 메시지 수신 정보 수정
    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
            content = @Content(mediaType = "*/*", schema = @Schema(implementation = ReadStatus.class))),
        @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")))
    })
    @PatchMapping("/{readStatusId}")
    @Parameter(name = "readStatusId", description = "메시지 수신 정보 ID", required = true)
    public ResponseEntity<ReadStatus> update(
        @Parameter(description = "수정할 읽음 상태 ID") @PathVariable UUID readStatusId,
        @Parameter(description = "수정할 읽음 상태 정보") @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatus updatedStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedStatus);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
            content = @Content(mediaType = "*/*"))
    })
    @GetMapping
    @Parameter(name = "userId", description = "유저 ID", required = true)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
        @Parameter(description = "조회할 User ID") @RequestParam UUID userId
    ) {
        // ReadStatus가 없을 시 Error 메시지(or 페이지)
        List<ReadStatus> userReadStatuses = readStatusService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userReadStatuses);
    }
}
