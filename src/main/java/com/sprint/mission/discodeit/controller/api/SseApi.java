package com.sprint.mission.discodeit.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "SSE", description = "SSE 이벤트 API")
public interface SseApi {

  @Operation(summary = "SSE 연결", description = "클라이언트가 서버와 SSE 연결을 시작합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SSE 연결 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패")
  })
  ResponseEntity<SseEmitter> connect(
      @Parameter(description = "마지막 이벤트 ID", required = false) UUID lastEventId,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader
  );
}
