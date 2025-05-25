package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelDTO> createPublicChannel(@RequestBody PublicChannelCreateRequest req) {
        Channel created = channelService.createPublicChannel(req);
        ChannelDTO channel = channelService.getChannel(created.getId());

        return ResponseEntity.ok(channel);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelDTO> createPrivateChannel(@RequestBody PrivateChannelCreateRequest req) {
        Channel created = channelService.createPrivateChannel(req);
        ChannelDTO channel = channelService.getChannel(created.getId());

        return ResponseEntity.ok(channel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelDTO> getChannel(@PathVariable UUID id) {
        ChannelDTO dto = channelService.getChannel(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChannelDTO>> getAllChannelsByUser(@PathVariable UUID userId) {
        List<ChannelDTO> list = channelService.getAllChannelsByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/public")
    public ResponseEntity<Void> updatePublicChannel(@RequestBody PublicChannelUpdateRequest req) {
        channelService.updateChannel(req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        channelService.deleteChannel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{channelId}/join")
    public ResponseEntity<Void> joinChannel(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ) {
        channelService.joinChannel(userId, channelId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{channelId}/leave")
    public ResponseEntity<Void> leaveChannel(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ) {
        channelService.leaveChannel(userId, channelId);
        return ResponseEntity.ok().build();
    }
}
