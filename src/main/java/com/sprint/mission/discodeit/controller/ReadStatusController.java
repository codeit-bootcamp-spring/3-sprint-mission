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

@Controller
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(
            path = "/create"
            , method = RequestMethod.POST
    )
    @ResponseBody
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatus createdStatus = readStatusService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @RequestMapping(
            path = "/update"
            , method = RequestMethod.PUT
    )
    @ResponseBody
    public ResponseEntity<ReadStatus> update(
            @RequestParam("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {

        ReadStatus updatedStatus = readStatusService.update(readStatusId, request);

        return ResponseEntity.status(HttpStatus.OK).body(updatedStatus);
    }

    @RequestMapping(
            path = "/search"
            , method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<ReadStatus>> search(
            @RequestParam("userId") UUID userId
    ) {

        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
    }
}
