package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> create(@RequestBody PublicChannelCreateRequest request) {
        System.out.println("ChannelController.create-public");
        return channelService.createChannel(request);
    }

    @Operation(summary = "비공개 채널 생성", description = "비공개 채널을 생성합니다.")
    @PostMapping("/private")
    public ResponseEntity<?> create(@RequestBody PrivateChannelCreateRequest request) {
        System.out.println("ChannelController.create-private");
        return channelService.createChannel(request);
    }

    @Operation(summary = "채널 삭제", description = "채널을 삭제합니다.")
    @DeleteMapping("/{channelId}")
    public ResponseEntity<?> removeChannel(@PathVariable UUID channelId) {
        System.out.println("ChannelController.removeChannel");
        return channelService.deleteChannel(channelId);
    }

    @Operation(summary = "채널 정보 수정", description = "채널 정보를 수정합니다.")
    @PatchMapping("/{channelId}")
    public ResponseEntity<?> changeName(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest request) {
        System.out.println("ChannelController.changeName");
        return channelService.update(channelId, request);
    }

    @Operation(summary = "유저가 참여중인 채널 목록 조회", description = "유저가 참여중인 채널 목록을 전체 조회합니다.")
    @GetMapping
    public ResponseEntity<?> findChannels(@RequestParam UUID userId) {
        System.out.println("ChannelController.findChannels");
        return channelService.findAllByUserId(userId);
    }
}
