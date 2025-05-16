package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : ReadStatusController
 * author         : doungukkim
 * date           : 2025. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 11.        doungukkim       최초 생성
 */
@RestController
@RequestMapping("api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;


    @GetMapping
    public ResponseEntity<?> findReadStatuses(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }


    @PostMapping
    public ResponseEntity<?> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @ResponseBody
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<?> updateTime(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.update(readStatusId, request);
    }
}
