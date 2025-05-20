package com.sprint.mission.discodeit.controller.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.binaryContent.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> getProfileContents(@RequestParam UUID ownerId) {
        BinaryContent binaryContents = binaryContentService.findProfileImageByOwnerId(ownerId);
        return ResponseEntity.ok().body(binaryContents);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> getBinaryContents(@RequestParam UUID ownerId) {
        List<BinaryContent> binaryContents = binaryContentService.findAllAttachmentsByOwnerId(ownerId);
        return ResponseEntity.ok().body(binaryContents);
    }
}
