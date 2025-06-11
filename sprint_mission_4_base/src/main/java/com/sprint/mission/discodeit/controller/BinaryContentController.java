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

import java.util.*;

@RequiredArgsConstructor
<<<<<<< HEAD
<<<<<<< HEAD
@RequestMapping("/api/binaryContent")
=======
@RequestMapping("/api/binarycontent")
>>>>>>> 3189145 (5월 15일 강의 결과물)
=======
@RequestMapping("/api/binaryContent")
>>>>>>> 6fae2e2 (Basic requirements for sprint4)
@Controller
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(
            value = "/find"
            , method = RequestMethod.GET
    )
    public ResponseEntity<BinaryContent> find(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }

    @RequestMapping(
<<<<<<< HEAD
<<<<<<< HEAD
            value = "/findAllByIdIn"
            , method = RequestMethod.GET
    )
=======
            value = "/findAll"
            , method = RequestMethod.GET)
>>>>>>> 3189145 (5월 15일 강의 결과물)
=======
            value = "/findAllByIdIn"
            , method = RequestMethod.GET
    )
>>>>>>> 989f80a (Sprint mission 4 requirements completed)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContent> binaryContentList = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.status(HttpStatus.OK).body(binaryContentList);
    }
}
