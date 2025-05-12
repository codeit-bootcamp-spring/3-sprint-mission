package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentFindRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
@RequestMapping("api/binary-content/*")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;


    @RequestMapping(path = "/find-attachment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAttachment(@RequestBody BinaryContentFindRequest request) {
        return binaryContentService.findAllByIdIn(request);
    }

    @ResponseBody
    @RequestMapping(path = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@RequestParam UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }
}
