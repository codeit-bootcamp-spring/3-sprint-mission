package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentFindRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : BinaryContentController
 * author         : doungukkim
 * date           : 2025. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 11.        doungukkim       최초 생성
 */
@Controller
@RequestMapping("api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;


    @GetMapping
    public ResponseEntity<?> findAttachment(@RequestParam List<UUID> binaryContentIds) {
        System.out.println("BinaryContentController.findAttachment");
        return binaryContentService.findAllByIdIn(binaryContentIds);
    }

    @ResponseBody
    @GetMapping(path = "/{binaryContentId}")
    public ResponseEntity<?> findBinaryContent(@PathVariable UUID binaryContentId) {
        System.out.println("BinaryContentController.findBinaryContent");
        return binaryContentService.find(binaryContentId);
    }
}
