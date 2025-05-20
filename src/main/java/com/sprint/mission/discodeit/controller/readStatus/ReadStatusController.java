package com.sprint.mission.discodeit.controller.readStatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.readStatus.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;
    private final ChannelService channelService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> createChannelReadStatus(@RequestBody ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        ReadStatus readStatus = readStatusService.createReadStatus(readStatusCreateRequestDto);
        return ResponseEntity.ok().body(readStatus);
    }

    @RequestMapping(path = "/modify", method = RequestMethod.PUT)
    public ResponseEntity<?> modifyStatus(@RequestBody ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        boolean result = readStatusService.updateReadStatus(readStatusUpdateRequestDto);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
        }
        return ResponseEntity.ok().body("Success");
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findReadStatuesByUserId(@PathVariable UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok().body(readStatuses);
    }

}
