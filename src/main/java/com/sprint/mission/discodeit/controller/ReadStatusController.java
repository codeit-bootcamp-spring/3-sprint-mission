package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

/**
 * 메시지 읽음 상태(ReadStatus) 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 * <p>읽음 상태 생성, 수정, 사용자별 조회 기능을 제공합니다.</p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusService readStatusService;
    private static final String CONTROLLER_NAME = "[ReadStatusController] ";

    /**
     * 읽음 상태를 생성합니다.
     * @param readStatusCreateRequest 읽음 상태 생성 요청 정보
     * @return 생성된 읽음 상태 DTO
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadStatusDto> create(
        @Valid @RequestBody ReadStatusCreateRequest readStatusCreateRequest
    ) {
        log.info(CONTROLLER_NAME + "읽음 상태 생성 시도: userId={}, channelId={}",
                readStatusCreateRequest.userId(), readStatusCreateRequest.channelId());
        ReadStatusDto readStatus = readStatusService.create(readStatusCreateRequest);
        log.info(CONTROLLER_NAME + "읽음 상태 생성 성공: id={}", readStatus.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatus);
    }

    /**
     * 읽음 상태를 수정합니다.
     * @param readStatusId 수정할 읽음 상태 ID
     * @param readStatusUpdateRequest 읽음 상태 수정 요청 정보
     * @return 수정된 읽음 상태 DTO
     */
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(
        @PathVariable UUID readStatusId,
        @Valid @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        log.info(CONTROLLER_NAME + "읽음 상태 수정 시도: id={}", readStatusId);
        ReadStatusDto updatedStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);
        log.info(CONTROLLER_NAME + "읽음 상태 수정 성공: id={}", readStatusId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedStatus);
    }

    /**
     * 특정 사용자의 읽음 상태 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 읽음 상태 DTO 목록
     */
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
        @RequestParam UUID userId
    ) {
        log.info(CONTROLLER_NAME + "사용자 읽음 상태 목록 조회 시도: userId={}", userId);
        List<ReadStatusDto> userReadStatuses = readStatusService.findAllByUserId(userId);
        log.info(CONTROLLER_NAME + "사용자 읽음 상태 목록 조회 성공: userId={}, 건수={}", userId, userReadStatuses.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userReadStatuses);
    }
}
