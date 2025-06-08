package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/* API 구현 절차
    1. 엔드포인트(End-point)
     - 엔드포인트는 URL과 HTTP 메서드로 구성됨
     - 엔드포인트는 다른 API와 겹치지 않는 유일한 값으로 정의할것.
     2. 요청
     - 요청으로부터 어떤 값을 받아야하는지 정의
     - 각 값을 HTTP 요청의 Header, Body 등 어떤 부분에서 어떻게 받을지 정의.
     3. 응답 ( 뷰 기반이 아닌 데이터 기반 응답으로 작성
     - 응답 상태 코드 정의
     - 응답 데이터 정의
     -(옵션) 응답 헤더 정의
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  /* 바이너리 파일 한개 다운로드 */
  @GetMapping(path = "/{binaryContentId}")
  @Override
  public ResponseEntity<BinaryContentDto> find(
      @PathVariable("binaryContentId") UUID binaryContentId
  ) {
    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContentDto);
  }

  //FIXME. 여기서 storage에서 byte[] 가져와야하지않나?
  /*추가!! 바이너리 파일 한개 다운로드  */
  @GetMapping(path = "/{binaryContentId}/download")
  @Override
  public ResponseEntity<?> download(
      @PathVariable("binaryContentId") UUID binaryContentId
  ) {
    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContentDto);
  }


  /* 바이너리 파일 여러개 다운로드 */
  @GetMapping
  @Override
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }

}
