package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/binary-contents")
@RestController
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;


    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> findBinaryContentById(@PathVariable("id") UUID contentId) {
        return ResponseEntity.ok(binaryContentService.findById(contentId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllBinaryContentByIds(@RequestBody List<UUID> contentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(contentIds));
    }

}
