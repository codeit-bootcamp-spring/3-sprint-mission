package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


/**
 * PackageName  : com.sprint.mission.discodeit.controller.api
 * FileName     : ChannelApi
 * Author       : dounguk
 * Date         : 2025. 6. 19.
 */


@Tag(name = "Channel 컨트롤러", description = "스프린트 미션5 채널 컨트롤러 엔트포인트들 입니다.")
@RequestMapping("api/channels")
public interface ChannelApi {

    @Operation(summary = "공개 채널 생성", description = "공개 채널을 생성합니다.")
    @PostMapping("/public")
    ResponseEntity<?> create(@Valid @RequestBody PublicChannelCreateRequest request);

    @Operation(summary = "비공개 채널 생성", description = "비공개 채널을 생성합니다.")
    @PostMapping("/private")
    ResponseEntity<?> create(@Valid @RequestBody PrivateChannelCreateRequest request);


    @Operation(summary = "채널 삭제", description = "채널을 삭제합니다.")
    @DeleteMapping("/{channelId}")
    ResponseEntity<?> removeChannel(@PathVariable @NotNull UUID channelId);


    @Operation(summary = "채널 정보 수정", description = "채널 정보를 수정합니다.")
    @PatchMapping("/{channelId}")
    ResponseEntity<?> update(@PathVariable UUID channelId,
                             @Valid @RequestBody ChannelUpdateRequest request);


    @Operation(summary = "유저가 참여중인 채널 목록 조회",
        description = "유저가 참여중인 채널 목록을 전체 조회합니다.")
    @GetMapping
    ResponseEntity<?> findChannels(@RequestParam UUID userId);
}

