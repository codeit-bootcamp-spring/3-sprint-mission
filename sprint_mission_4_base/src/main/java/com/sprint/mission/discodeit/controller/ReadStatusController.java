package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/readstatus")
@Controller
public class ReadStatusController {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ReadStatusService readStatusService;

    // 특정 채널 메시지 수신 정보 생성
    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST
    )
    @ResponseBody
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest readStatusCreateRequest
    ) {
        ReadStatus readStatus = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    // 특정 채널 메시지 수신 정보 수정
    @RequestMapping(
            path = "/update",
            method = RequestMethod.PUT
    )
    @ResponseBody
    public ResponseEntity<ReadStatus> update(
            @RequestParam UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatus updatedStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedStatus);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(
            path = "/findAllByUserId",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        // ReadStatus가 없을 시 Error 메시지(or 페이지)
        List<ReadStatus> userReadStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userReadStatuses);
    }
}
