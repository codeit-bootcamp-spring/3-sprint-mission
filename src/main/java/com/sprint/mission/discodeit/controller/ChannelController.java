package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channels")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @Operation(summary = "public 채널 생성")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping("/public")
    public ResponseEntity<ChannelDTO> createPublicChannel(@RequestBody PublicChannelCreateRequest req) {
        Channel created = channelService.createPublicChannel(req);
        ChannelDTO channel = channelService.getChannel(created.getId());

        return ResponseEntity.ok(channel);
    }

    @Operation(summary = "private 채널 생성")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping("/private")
    public ResponseEntity<ChannelDTO> createPrivateChannel(@RequestBody PrivateChannelCreateRequest req) {
        Channel created = channelService.createPrivateChannel(req);
        ChannelDTO channel = channelService.getChannel(created.getId());

        return ResponseEntity.ok(channel);
    }

    @Operation(summary = "단일 채널 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<ChannelDTO> getChannel(@PathVariable UUID id) {
        ChannelDTO dto = channelService.getChannel(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "사용자가 접속한 전체 채널 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChannelDTO>> getAllChannelsByUser(@PathVariable UUID userId) {
        List<ChannelDTO> list = channelService.getAllChannelsByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "public 채널 업데이트")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/public")
    public ResponseEntity<Void> updatePublicChannel(@RequestBody PublicChannelUpdateRequest req) {
        channelService.updateChannel(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채널 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        channelService.deleteChannel(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채널 접속")
    @ApiResponse(responseCode = "200",  description = "접속 성공")
    @PostMapping("/{channelId}/join")
    public ResponseEntity<Void> joinChannel(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ) {
        channelService.joinChannel(userId, channelId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채널 탈퇴")
    @ApiResponse(responseCode = "200",  description = "탈퇴 성공")
    @PostMapping("/{channelId}/leave")
    public ResponseEntity<Void> leaveChannel(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ) {
        channelService.leaveChannel(userId, channelId);
        return ResponseEntity.ok().build();
    }
}
