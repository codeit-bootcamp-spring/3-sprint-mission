package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "BinaryContent", description = "이진 컨텐츠 API")
public interface BinaryContentApi {

    @Operation(summary = "단일 이진 컨텐츠 조회", description = "binaryContentId로 이진 컨텐츠를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BinaryContentDto> find(
        @Parameter(description = "이진 컨텐츠 ID") UUID binaryContentId
    );

    @Operation(summary = "다중 이진 컨텐츠 조회", description = "binaryContentIds로 여러 이진 컨텐츠를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
        @Parameter(description = "이진 컨텐츠 ID 목록") List<UUID> binaryContentIds
    );

    @Operation(summary = "이진 컨텐츠 다운로드", description = "binaryContentId로 이진 파일을 다운로드합니다.")
    @ApiResponse(responseCode = "200", description = "다운로드 성공")
    ResponseEntity<?> download(
        @Parameter(description = "이진 컨텐츠 ID") UUID binaryContentId
    );
}
