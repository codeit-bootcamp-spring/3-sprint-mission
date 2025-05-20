package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReaduStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-status")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReaduStatusService readuStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> createReadStatus(@RequestBody CreateReadStatusRequest createReadStatusRequest) {
        ReadStatus readStatus = readuStatusService.create(createReadStatusRequest);
        return ResponseEntity.ok(readStatus);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET) // api/read-status로 들어오는 요청에서 {id}는 동적인 값으로 처리
    public ResponseEntity<List<ReadStatus>> findAllReadStatusByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(readuStatusService.findAllByUserId(userId));
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<List<ReadStatus>> updateReadStatus(@PathVariable("channelId") UUID channelId) {
        List<ReadStatus> readStatusList = readuStatusService.findAll();
        readStatusList.stream()
                .filter((readStatus) -> readStatus.getChannelId().equals(channelId))
                .forEach(readStatus -> readStatus.update(Instant.now()));
        return ResponseEntity.ok(readStatusList);

    }


}
