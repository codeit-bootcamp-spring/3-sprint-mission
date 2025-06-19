package com.sprint.mission.discodeit.controller.api;


import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
/**
 * PackageName  : com.sprint.mission.discodeit.controller.api
 * FileName     : ReadStatusApi
 * Author       : dounguk
 * Date         : 2025. 6. 19.
 */


@Tag(name = "Read Status 컨트롤러", description = "스프린트 미션5 유저 상태 컨트롤러 엔트포인트들 입니다.")
@RequestMapping("api/readStatuses")
public interface ReadStatusApi {

    @Operation(summary = "사용자의 읽음 상태 목록 조회", description = "사용자의 읽음 상태 목록을 전체 조회 합니다.")
    @GetMapping
    ResponseEntity<?> find(@RequestParam UUID userId);

    @Operation(summary = "사용자의 읽음 상태 생성", description = "사용자의 읽음 상태 생성 합니다.")
    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody ReadStatusCreateRequest request);

    @Operation(summary = "사용자의 읽음 상태 수정", description = "사용자의 읽음 상태 시간을 수정 합니다.")
    @PatchMapping("/{readStatusId}")
    ResponseEntity<?> update(
        @PathVariable UUID readStatusId,
        @Valid @RequestBody ReadStatusUpdateRequest request);
}

