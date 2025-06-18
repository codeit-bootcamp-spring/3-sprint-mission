package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@RestController
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메세지 수신 정보 생성( POST )
    @PostMapping
    public ResponseEntity<ReadStatusDto> create(
        @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusDto createdReadStatus = readStatusService.create(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdReadStatus);
    }


    // 특정 채널의 메세지 수신 정보 수정( PATCH )
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(
        @PathVariable("readStatusId") UUID readStatusId,
        @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedReadStatus);
    }


    // 특정 사용자의 메세지 수신 정보 조회( GET )
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
        @RequestParam("userId") UUID userId
    ) {
        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);

        // 리스트가 비었을 시 HTTP 상태 코드 204 발생
        if (readStatuses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // 값이 존재하면 정상 처리 반응
        return ResponseEntity.status(HttpStatus.OK)
            .body(readStatuses);
    }
}
