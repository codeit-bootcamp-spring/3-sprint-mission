package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(path = "/findAll",method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<BinaryContent>> findAll(@RequestParam List<UUID> binaryContentIds){
        List<BinaryContent> allByIdIn = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok().body(allByIdIn);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<BinaryContent> find(@RequestParam UUID binaryContentId){
        BinaryContent allByIdIn = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok().body(allByIdIn);
    }

}
