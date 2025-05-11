package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusRequestDTO readStatusRequestDTO) {
        ReadStatus createdReadStatus = readStatusService.create(readStatusRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ReadStatusResponseDTO> findById(@RequestParam UUID readStatusId) {
        ReadStatusResponseDTO foundReadStatus = readStatusService.findById(readStatusId);

        return ResponseEntity.status(HttpStatus.OK).body(foundReadStatus);
    }

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ReadStatusResponseDTO>> findAll() {
        List<ReadStatusResponseDTO> allReadStatus = readStatusService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(allReadStatus);
    }

    @RequestMapping(path = "/findAllByUser", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ReadStatusResponseDTO>> findByUserId(@RequestParam UUID userId) {
        List<ReadStatusResponseDTO> foundReadStatus = readStatusService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(foundReadStatus);
    }

    @RequestMapping(path = "/update", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ReadStatusResponseDTO> update(@RequestParam UUID readStatusId,
                                        @RequestBody ReadStatusRequestDTO readStatusRequestDTO) {
        ReadStatusResponseDTO updatedReadStatus = readStatusService.update(readStatusId, readStatusRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteById(@RequestParam UUID readStatusId) {
        readStatusService.deleteById(readStatusId);

        return ResponseEntity.status(HttpStatus.OK).body("[Success]: 메시지 수신 정보 삭제 성공!");
    }
}
