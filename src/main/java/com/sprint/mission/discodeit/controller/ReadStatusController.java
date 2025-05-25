package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatuses")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @Operation(summary = "읽음 상태 생성")
    @ApiResponse(responseCode = "201",  description = "생성 성공")
    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest req) {
        ReadStatus readStatus = readStatusService.create(req);
        return ResponseEntity.ok(readStatus);
    }

    @Operation(summary = "채널 별 읽음 상태 조회")
    @ApiResponse(responseCode = "200",  description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<List<ReadStatus>> findByChannelId (
            @PathVariable UUID id
    ) {
        List<ReadStatus> readStatuses = readStatusService.findAllByChannelId(id);
        return ResponseEntity.ok(readStatuses);
    }

    @Operation(summary = "읽음 상태 수정")
    @ApiResponse(responseCode = "200",  description = "수정 성공")
    @PutMapping("update")
    public ResponseEntity<ReadStatus> update(@RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus readStatus = readStatusService.update(readStatusUpdateRequest);
        return ResponseEntity.ok(readStatus);
    }
}