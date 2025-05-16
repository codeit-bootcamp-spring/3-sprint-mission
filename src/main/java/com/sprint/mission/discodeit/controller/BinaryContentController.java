package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
@ResponseBody
@RequestMapping("/api/binaryContent")
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  /* 바이너리 파일 한개 다운로드 */
  @Operation(summary = "첨부 파일 조회")
  @Parameters({
      @Parameter(
          name = "binaryContentId",
          description = "조회할 첨부 파일 ID",
          required = true,
          in = ParameterIn.PATH,
          schema = @Schema(type = "string", format = "uuid")
      )
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공", content = @Content(schema = @Schema(implementation = BinaryContent.class))),
      @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음", content = @Content(examples = {
          @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")
      }))
  })
  @GetMapping(path = "find")
  public ResponseEntity<BinaryContent> find(
      @RequestParam String binaryContentId
  ) {
    BinaryContent binaryContent = binaryContentService.find(parseStringToUuid(binaryContentId));
    return ResponseEntity.ok().body(binaryContent);
  }

  /* 바이너리 파일 여러개 다운로드 */
  @Operation(summary = "여러 첨부 파일 조회")
  @Parameters({
      @Parameter(
          name = "binaryContentIds",
          description = "조회할 첨부 파일 ID 목록",
          required = true,
          in = ParameterIn.QUERY,
          array = @ArraySchema(schema = @Schema(type = "string", format = "uuid"))
      )
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))))
  })
  @PostMapping(path = "/findAllByIdIn")
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestBody List<UUID> binaryContentIds
  ) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok().body(binaryContents);
  }


  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* String 타입 -> UUID 타입으로 변경 */
  private UUID parseStringToUuid(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("올바른 파라미터 형식이 아닙니다.");
    }
  }

}
