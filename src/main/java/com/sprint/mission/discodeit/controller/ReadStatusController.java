package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
@Controller
public class ReadStatusController {

    private final ReadStatusService readStatusService;
    private final UserService userService;

    /**
     * 특정 채널의 메시지 수신 정보 생성
     *
     * @param readStatusCreateRequestDTO 메시지 수신 정보 생성 요청 DTO
     * @return 생성된 메시지 수신 정보 (HTTP 201 CREATED)
     */
    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequestDTO readStatusCreateRequestDTO
    ) {
        // 메시지 수신 정보 생성
        ReadStatus createdReadStatus = readStatusService.create(readStatusCreateRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReadStatus);
    }

    /**
     * 특정 채널의 메시지 수신 정보 수정
     *
     * @param readStatusId 조회할 메시지 수신 정보 ID
     * @param readStatusUpdateRequestDTO 메시지 수신 정보 수정 요청 DTO
     * @return 수정된 메시지 수신 정보 (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/update",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<ReadStatus> update(
            @RequestParam UUID readStatusId,
            @RequestBody ReadStatusUpdateRequestDTO readStatusUpdateRequestDTO
    ) {
        // 메시지 수신 정보 수정
        ReadStatus updatedReadStatus = readStatusService.update(readStatusId,readStatusUpdateRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedReadStatus);
    }

    /**
     * 특정 사용자의 메시지 수신 정보 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 메시지 수신 정보 목록 (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/findAll",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<ReadStatus>> findAllByUserId (
            @RequestParam UUID userId
    ) {
        // 유저 유효성 검사
        userService.find(userId);

        // 유저의 메시지 수신 정보 조회
        List<ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatusList);
    }
}
