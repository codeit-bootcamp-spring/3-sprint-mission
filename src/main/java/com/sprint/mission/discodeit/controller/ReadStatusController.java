package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/read-status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
        return ResponseEntity.ok(readStatusService.create(request));
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ReadStatus> update(@RequestBody ReadStatusUpdateRequest request) {
        return ResponseEntity.ok(readStatusService.update(request));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
