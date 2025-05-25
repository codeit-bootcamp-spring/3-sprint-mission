package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/read-status")
@Controller
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // 특정 채널 메시지 수신 정보 생성
    @RequestMapping(path = "/create"
            , method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest readStatusCreateRequest
    ) {
        ReadStatus created = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 특정 채널 메시지 수신 정보 수정
    @RequestMapping(path = "/update"
            , method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ReadStatus> update(
            @RequestParam("id") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatus updated = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    // 특정 사용자 메시지 수신 정보 조회
    @RequestMapping(path = "/find-all-by-user"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ReadStatus>> findAllByUser(
            @RequestParam("userId") UUID userId
    ) {
        List<ReadStatus> statuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(statuses);
    }
}
