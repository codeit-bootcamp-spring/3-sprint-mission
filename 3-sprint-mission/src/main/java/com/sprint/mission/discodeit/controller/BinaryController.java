package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentAPI;
import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@RestController
public class BinaryController implements BinaryContentAPI {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping
    public ResponseEntity<List<BinaryContentDTO>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {

        List<BinaryContentDTO> result =
                binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(
            value = "/{binaryContentId}"
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BinaryContentDTO> find(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {

        BinaryContentDTO result =
                binaryContentService.find(binaryContentId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(
            value = "/{binaryContentId}/download"
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> download(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        BinaryContentDTO binaryContentDTO = binaryContentService.find(binaryContentId);
        return ResponseEntity.status(HttpStatus.OK).body(
                binaryContentStorage.download(binaryContentDTO)
        );
    }
}
