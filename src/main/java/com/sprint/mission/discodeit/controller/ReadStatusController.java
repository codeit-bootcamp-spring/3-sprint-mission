package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RequestMapping("/api") // 기본 경로 설정
@Controller
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @RequestMapping(
            path = "/user/{userId_path}/channel/{channelId_path}/readstatus", // 경로 변수명 변경하여 DTO와 구분
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> createReadStatusForChannel(
            @PathVariable("userId_path") UUID userIdFromPath,
            @PathVariable("channelId_path") UUID channelIdFromPath,
            @RequestBody ReadStatusCreateRequest request
    ) {
        // 경로 변수와 DTO의 ID 일치 여부 검증 (선택적)
        if (!request.userId().equals(userIdFromPath) || !request.channelId().equals(channelIdFromPath)) {
            return ResponseEntity.badRequest().body("{\"error\": \"User ID or Channel ID in path does not match request body.\"}");
        }
        try {
            ReadStatus createdReadStatus = readStatusService.createReadStatus(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
        } catch (NoSuchElementException e) { // User 또는 Channel이 없을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (IllegalStateException e) { // 이미 ReadStatus가 존재할 경우
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to create read status: " + e.getMessage() + "\"}");
        }
    }

    // 특정 채널의 메시지 수신 정보 수정 (ReadStatus ID 기반)
    @RequestMapping(
            path = "/readstatus/{readStatusId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> updateReadStatusForChannel(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        try {
            ReadStatus updatedReadStatus = readStatusService.updateReadStatus(readStatusId, request);
            return ResponseEntity.ok(updatedReadStatus);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to update read status: " + e.getMessage() + "\"}");
        }
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(
            path = "/user/{userId}/readstatus",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> getUserReadStatuses(@PathVariable("userId") UUID userId) {
        try {
            List<ReadStatus> readStatuses = readStatusService.findAllReadStatusesByUserId(userId);
            return ResponseEntity.ok(readStatuses);
        } catch (NoSuchElementException e) { // 사용자가 없을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to retrieve read statuses: " + e.getMessage() + "\"}");
        }
    }
}