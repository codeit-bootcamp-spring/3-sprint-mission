package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.controller.api
 * FileName     : MessageApi
 * Author       : dounguk
 * Date         : 2025. 6. 19.
 */


@Tag(name = "Message 컨트롤러", description = "스프린트 미션5 메세지 컨트롤러 엔트포인트들 입니다.")
@RequestMapping("api/messages")
public interface MessageApi {

    @Operation(summary = "심화 채널 메세지 목록 조회", description = "메세지를 수정 합니다.")
    @GetMapping
    ResponseEntity<?> findMessagesInChannel(@RequestParam UUID channelId,
                                            @RequestParam(required = false) Instant cursor,
                                            Pageable pageable);

    @Operation(summary = "메세지 생성", description = "메세지를 생성 합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> creatMessage(
        @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachmentFiles
    );

    @Operation(summary = "메세지 삭제", description = "메세지를 삭제 합니다.")
    @DeleteMapping(path = "/{messageId}")
    ResponseEntity<?> deleteMessage(@PathVariable @NotNull UUID messageId);

    @Operation(summary = "메세지 수정", description = "메세지를 수정 합니다.")
    @PatchMapping(path = "/{messageId}")
    ResponseEntity<?> updateMessage(
        @PathVariable UUID messageId,
        @Valid @RequestBody MessageUpdateRequest request);
}

