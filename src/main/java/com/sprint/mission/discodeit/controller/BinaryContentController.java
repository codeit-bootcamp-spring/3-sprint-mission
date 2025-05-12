package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/binary-content")
@Controller
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 다건 조회 : 바이너리 파일 여러 개 조회( GET )
    @RequestMapping(
            // 다건 조회는 현재 경로(/api/binary-content) 그대로 사용
            path = ""
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<BinaryContent>> findBinaryContents(
            @RequestParam(value = "ids", required = false) List<UUID> ids
    ) {
        // 조회한 바이너리 파일이 없는 상황
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<BinaryContent> binaries = binaryContentService.findAllByIdIn(ids);

        // 바이너리 파일을 리스트로 생성한 것을 조회했는데 없는 상황
        if (binaries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(binaries);
    }

    // 단건 조회 : 바이너리 파일을 1개 조회( GET )
    @RequestMapping(
            path = "/single"
            , method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<byte[]> findBinaryContent(
            @RequestParam("id") UUID id
    ) {
        BinaryContent binary = binaryContentService.find(id);

        // 조회한 바이너리 파일이 없을 경우
        if (binary == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // BinaryContent의 헤더 정보 설정( 사용자 관점에서 처리하기 용이하도록 )
        // 사용자 편의성 및 일관된 파일 처리를 위함
        HttpHeaders headers = new HttpHeaders();

        // 파일 타입 헤더 설정 : 파일의 MIME 타입을 받은 후 적절한 객체로 변환
        headers.setContentType(MediaType.parseMediaType(binary.getFileType()));
        // 응답 처리 헤더 설정 : 파일을 다운로드하도록 지정하며, 해당 파일의 이름을 정의함
        headers.setContentDisposition(ContentDisposition.attachment().filename(binary.getFileName()).build());

        // 파일의 실제 데이터, 설정된 헤더 정보 포함, 상태코드( 200 ) 반환
        return new ResponseEntity<>(binary.getFileData(), headers, HttpStatus.OK);
    }
}