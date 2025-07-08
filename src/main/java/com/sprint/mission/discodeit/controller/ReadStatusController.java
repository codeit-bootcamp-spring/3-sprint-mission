package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@RestController
@Slf4j
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메세지 수신 정보 생성( POST )
    @PostMapping
    public ResponseEntity<ReadStatusDto> create(
        @RequestBody ReadStatusCreateRequest request
    ) {
        log.info("읽음 상태 생성 요청 : {}", request);
        ReadStatusDto createdReadStatus = readStatusService.create(request);
        log.debug("읽음 상태 생성 응답 : {}", createdReadStatus);
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
        log.info("읽음 상태 수정 요청 : id = {}, request = {}", readStatusId, request);
        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
        log.debug("읽음 상태 수정 응답 : {}", updatedReadStatus);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedReadStatus);
    }


    // 특정 사용자의 메세지 수신 정보 조회( GET )
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
        @RequestParam("userId") UUID userId
    ) {
        log.info("사용자별 읽음 상태 목록 조회 요청 : userId = {}", userId);
        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);

        // 리스트가 비었을 시 HTTP 상태 코드 204 발생
        if (readStatuses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        log.debug("사용자별 읽음 상태 목록 조회 응답 : count = {}", readStatuses.size());
        // 값이 존재하면 정상 처리 반응
        return ResponseEntity.status(HttpStatus.OK)
            .body(readStatuses);
    }
}
