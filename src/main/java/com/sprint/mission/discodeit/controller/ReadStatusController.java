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
@RequestMapping("/api/readStatus")
@Controller
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 메시지 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createReadStatus(
            @RequestBody ReadStatusCreateRequest request) {
        try {
            ReadStatus createdReadStatus = readStatusService.createReadStatus(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
        } catch (NoSuchElementException e) { // User 또는 Channel이 없을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create read status: " + e.getMessage());
        }
    }

    // 메시지 수신 정보 수정
    @RequestMapping(path = "/{readStatusId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateReadStatus(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request) {
        try {
            ReadStatus updatedReadStatus = readStatusService.updateReadStatus(readStatusId, request);
            return ResponseEntity.ok(updatedReadStatus);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("ReadStatus with id " + readStatusId + " not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update read status: " + e.getMessage());
        }
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getUserReadStatuses(@RequestParam("userId") UUID userId) {
        try {
            List<ReadStatus> readStatuses = readStatusService.findAllReadStatusesByUserId(userId);
            return ResponseEntity.ok(readStatuses);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve read statuses: " + e.getMessage());
        }
    }
}