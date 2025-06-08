package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@RestController
public class ReadController {
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatus readStatus = readStatusService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatus);
    }

    @PatchMapping(
            value = "/{readStatusId}"
    )
    public ResponseEntity<ReadStatus> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateDTO
    ) {
        ReadStatus readStatusUpdate = readStatusService.update(readStatusId, readStatusUpdateDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatusUpdate);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusDTO>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
//        List<ReadStatus> readStatus = readStatusService.findAll();
        List<ReadStatusDTO> readStatusDTO = readStatusService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatusDTO);
    }
}
