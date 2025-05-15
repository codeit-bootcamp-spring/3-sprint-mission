package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ReadStatus> readStatusCreate(@RequestBody CreateReadStatusRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> readStatusUpdate(@RequestBody UpdateReadStatusRequest request) {
        readStatusService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body("수정완료!");
    }

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ReadStatus>> readStatusUpdate(@RequestParam UUID userId) {
        List<ReadStatus> allByUserId = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
    }





}
