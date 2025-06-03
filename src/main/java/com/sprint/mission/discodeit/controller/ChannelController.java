package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller fileName       : ChannelController
 * author         : doungukkim date           : 2025. 5. 10. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 5. 10.        doungukkim최초 생성
 */
@Tag(name = "Channel 컨트롤러", description = "스프린트 미션5 채널 컨트롤러 엔트포인트들 입니다.")
@RestController
@RequestMapping("api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "공개 채널 생성", description = "공개 채널을 생성합니다.")
    @PostMapping("/public")
    public ResponseEntity<?> create(@Valid @RequestBody PublicChannelCreateRequest request) {
        return ResponseEntity.status(201).body(channelService.createChannel(request));
    }

    @Operation(summary = "비공개 채널 생성", description = "비공개 채널을 생성합니다.")
    @PostMapping("/private")
    public ResponseEntity<?> create(@RequestBody PrivateChannelCreateRequest request) {
        return ResponseEntity.status(201).body(channelService.createChannel(request));
    }

    @Operation(summary = "채널 삭제", description = "채널을 삭제합니다.")
    @DeleteMapping("/{channelId}")
    public ResponseEntity<?> removeChannel(@PathVariable UUID channelId) {
        if (channelService.deleteChannel(channelId)) {
            return ResponseEntity.status(204).build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
    }

    @Operation(summary = "채널 정보 수정", description = "채널 정보를 수정합니다.")
    @PatchMapping("/{channelId}")
    public ResponseEntity<?> update(
            @PathVariable UUID channelId,
            @Valid @RequestBody ChannelUpdateRequest request) {
        return ResponseEntity.status(200).body(channelService.update(channelId, request));
    }

    @Operation(summary = "유저가 참여중인 채널 목록 조회", description = "유저가 참여중인 채널 목록을 전체 조회합니다.")
    @GetMapping
    public ResponseEntity<?> findChannels(@RequestParam UUID userId) {
        return ResponseEntity.status(200).body(channelService.findAllByUserId2(userId));
    }
}
