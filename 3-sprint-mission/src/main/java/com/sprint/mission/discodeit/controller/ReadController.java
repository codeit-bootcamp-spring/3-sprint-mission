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
@RequestMapping("/api/read")
@ResponseBody
@Controller
public class ReadController {
    private final ReadStatusService readStatusService;

    @RequestMapping(
            value = "/create"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest readStatusCreateDTO
    ) {
        ReadStatus readStatus = readStatusService.create(readStatusCreateDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatus);
    }

    @RequestMapping(
            value = "/update"
            , method = RequestMethod.PUT
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> update(
            @RequestParam UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateDTO
    ) {
        ReadStatus readStatusUpdate = readStatusService.update(readStatusId, readStatusUpdateDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatusUpdate);
    }

    @RequestMapping(
            value = "/findAll"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam UUID userId
    ) {
//        List<ReadStatus> readStatus = readStatusService.findAll();
        List<ReadStatus> readStatus = readStatusService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatus);
    }
}
