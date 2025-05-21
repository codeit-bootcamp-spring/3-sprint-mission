package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(
            path = "/find"
            , method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<BinaryContent> find(
            @RequestParam("binaryContentId") UUID binaryContentId
    ) {
        BinaryContent content = binaryContentService.find(binaryContentId);

        return ResponseEntity.status(HttpStatus.OK).body(content);
    }

    @RequestMapping(
            path = "/findAll"
            , method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<BinaryContent>> findAll(
            @RequestParam("ids") List<UUID> ids
    ) {
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);

        return ResponseEntity.status(HttpStatus.OK).body(contents);
    }


}
