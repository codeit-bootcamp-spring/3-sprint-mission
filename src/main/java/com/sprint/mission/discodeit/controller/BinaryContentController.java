package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
@Controller
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value ="/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.findBinaryContentById(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    // BinaryContent 다중 조회
    @RequestMapping(value ="/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAll(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllBinaryContent(binaryContentIds);
        return ResponseEntity.ok(binaryContents);
    }
}
