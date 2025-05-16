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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
@Controller
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 단일 파일
    @RequestMapping(path = "/find"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BinaryContent> findBinaryContent(
            @RequestParam("binaryContentId")UUID binaryContentId
    ) {
        BinaryContent content = binaryContentService.find(binaryContentId);
        return ResponseEntity.status(HttpStatus.OK).body(content);
    }

    // 여러 개 파일
    @RequestMapping(path = "/findAllByIdIn"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    ) {
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.status(HttpStatus.OK).body(contents);
    }
}