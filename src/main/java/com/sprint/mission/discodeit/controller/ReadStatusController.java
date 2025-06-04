package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
    @ApiResponses({
            @ApiResponse(responseCode = "201",  description = "Message 읽음 상태가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함"),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatus readStatus = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatus);
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatuses);
    }

    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음")
    })
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatus> update(@PathVariable("readStatusId") UUID readStatusId,
                                             @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus updatedReadStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedReadStatus);
    }
}