package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Read Status 컨트롤러", description = "스프린트 미션5 유저 상태 컨트롤러 엔트포인트들 입니다.")
@RestController
@RequestMapping("api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;


    @Operation(summary = "사용자의 읽음 상태 목록 조회", description = "사용자의 읽음 상태 목록을 전체 조회 합니다.")
    @GetMapping
    public ResponseEntity<?> findReadStatuses(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    @Operation(summary = "사용자의 읽음 상태 생성", description = "사용자의 읽음 상태 생성 합니다.")
    @PostMapping
    public ResponseEntity<?> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @Operation(summary = "사용자의 읽음 상태 수정", description = "사용자의 읽음 상태 시간을 수정 합니다.")
    @ResponseBody
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<?> updateTime(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.update(readStatusId, request);
    }
}
