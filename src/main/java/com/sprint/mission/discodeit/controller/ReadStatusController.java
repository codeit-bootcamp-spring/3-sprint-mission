package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
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

@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@RestController
@Tag(name = "ReadStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    /**
     * 특정 채널의 메시지 수신 정보 생성
     */
    @PostMapping
    public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusCreateRequest request) {
        ReadStatusDto createdReadStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
    }

    /**
     * 특정 채널의 메시지 수신 정보 수정
     */
    @PatchMapping(path = "{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(updatedReadStatus);
    }

    /**
     * 특정 사용자의 메시지 수신 정보 조회
     */
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatuses);
    }
}
