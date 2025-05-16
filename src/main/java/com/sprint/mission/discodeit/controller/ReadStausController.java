package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/read-status")
@Controller
public class ReadStausController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @RequestMapping("/create")
    @ResponseBody
    public ResponseEntity<ReadStatus> createReadStatus(
            @RequestBody ReadStatusCreateRequest request
    ){
        ReadStatus created = readStatusService.createReadStatus(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 특정 채널의 메시지 수신 정보 수정
    @RequestMapping("/update")
    @ResponseBody
    public ResponseEntity<ReadStatus> update(@RequestBody ReadStatusUpdateRequest request) {
        ReadStatus updated = readStatusService.updateReadStatus(request);
        return ResponseEntity.ok(updated);
    }

    // 특정 사용자의 메시지 수신 정보 목록 조회
    @RequestMapping("/user")
    @ResponseBody
    public ResponseEntity<List<ReadStatus>> getByUserId(@RequestParam UUID userId) {
        List<ReadStatus> result = readStatusService.findReadStatusByUserId(userId);
        return ResponseEntity.ok(result);
    }

}
