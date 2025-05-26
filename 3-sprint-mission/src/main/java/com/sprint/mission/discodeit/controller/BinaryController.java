package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@RestController
@Tag(
        name = "BinaryContent"
        , description = "첨부 파일 API"
)
public class BinaryController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentRepository binaryContentRepository;

    @Operation(
            summary = "여러 첨부 파일 조회"
            , operationId = "findAllByIdIn"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공", content = @Content(schema = @Schema(implementation = BinaryContent.class)))
            }
    )
    @Parameter(
            name = "binaryContentIds"
            , in = ParameterIn.QUERY
            , description = "조회할 첨부 파일 ID 목록"
            , required = true
            , content = @Content(array = @ArraySchema(
                    schema = @Schema(type = "array", format = "uuid"))
    )
    )
    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {

        List<BinaryContent> result =
                binaryContentRepository.findAllByIdIn(binaryContentIds);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "첨부 파일 조회"
            , operationId = "find"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공", content = @Content(schema = @Schema(implementation = BinaryContent.class))),
                    @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음", content = @Content(schema = @Schema(example = "BinaryContent with id {binaryContentId} not found")))
            }
    )
    @Parameter(
            name = "binaryContent"
            , in = ParameterIn.PATH
            , description = "조회할 첨부 파일 ID"
            , required = true
            ,schema = @Schema(type = "string", format = "uuid")
    )
    @GetMapping(
            value = "/{binaryContentId}"
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Optional<BinaryContent>> find(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {

        Optional<BinaryContent> result =
                binaryContentRepository.findById(binaryContentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private List<BinaryContentCreateRequest> resolveBinaryContentRequest(MultipartFile[] files) {

        List<BinaryContentCreateRequest> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    BinaryContentCreateRequest dto = new BinaryContentCreateRequest(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                    );

                    attachments.add(dto);
                } catch (IOException e) {
                    throw new RuntimeException("파일 처리 중 오류 발생: " + file.getOriginalFilename(), e);
                }
            }
        }

        return attachments;

    }
}
