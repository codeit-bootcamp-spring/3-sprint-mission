package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
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

@RequiredArgsConstructor
@RequestMapping("/api/read-status")
@Controller
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메세지 수신 정보 생성( POST )
    @RequestMapping(
            path = "/channel/{channelId}/receive"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<ReadStatus> createReadStatus(
            @PathVariable UUID channelId,
            @RequestBody ReadStatusCreateRequest request
    ) {
        // ReadStatusService의 create은 단일 파라미터임으로, RequestDTO에 대상 ID 포함
        ReadStatusCreateRequest readStatusInfo = new ReadStatusCreateRequest(
                channelId,
                request.getUserId(),
                request.getLastReadAt()
        );

        ReadStatus readStatus = readStatusService.create(readStatusInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }


    // 특정 채널의 메세지 수신 정보 수정( PUT )
    @RequestMapping(
            path = "/channel/{channelId}/receive/{readStatusId}"
            , method = RequestMethod.PUT
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<ReadStatus> updateReadStatus(
            @PathVariable UUID channelId,
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        // 채널 경로 명시( 지정 ) 및 검증
        ReadStatus target = readStatusService.find(readStatusId);
        if (!target.getChannelId().equals(channelId)) {
            throw new IllegalArgumentException("Channel ID가 일치하지 않습니다");
        }

        // channelId : 특정 채널에 대한 경로를 명시
        ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);

        return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
    }


    // 특정 사용자의 메세지 수신 정보 조회( GET )
    @RequestMapping(
            path = "/user/{userId}/receive"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ReadStatus>> findReadStatusByUserId(
            @PathVariable UUID userId
    ) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);

        // 리스트가 비었을 시 HTTP 상태 코드 204 발생
        if (readStatuses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // 값이 존재하면 정상 처리 반응
        return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
    }
}
