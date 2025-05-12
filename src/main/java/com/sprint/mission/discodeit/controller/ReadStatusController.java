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
@Controller
@RequestMapping("api/readStatus/*")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

//    [v] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
//    [v] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
//    [ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.

    @ResponseBody
    @RequestMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @ResponseBody
    @RequestMapping(value = "/updateTime", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTime(@RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.update(request);
    }

    @ResponseBody
    @RequestMapping(value = "/findReadStatuses")
    public ResponseEntity<?> findReadStatuses(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}
