package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
@Slf4j
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusCreateRequest request) {
        log.info("읽음 상태 생성 요청: userId={}, channelId={}, lastReadAt={}",
            request.userId(), request.channelId(), request.lastReadAt());

        ReadStatusDto createdReadStatus = readStatusService.create(request);

        log.info("읽음 상태 생성 완료: readStatusId={}", createdReadStatus.id());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdReadStatus);
    }

    @PatchMapping(path = "{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(
        @PathVariable("readStatusId") UUID readStatusId,
        @RequestBody ReadStatusUpdateRequest request) {
        log.info("읽음 상태 업데이트 요청: readStatusId={}, newLastReadAt={}",
            readStatusId, request.newLastReadAt());

        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);

        log.info("읽음 상태 업데이트 완료: readStatusId={}", updatedReadStatus.id());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedReadStatus);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
        @RequestParam("userId") UUID userId) {
        log.info("사용자 읽음 상태 전체 조회 요청: userId={}", userId);

        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);

        log.info("조회된 읽음 상태 개수: {}", readStatuses.size());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(readStatuses);
    }
}