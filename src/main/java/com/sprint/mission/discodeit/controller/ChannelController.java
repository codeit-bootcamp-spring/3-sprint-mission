package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping(path = "/public")
    public ResponseEntity<Channel> createPublic(@RequestBody PublicChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PostMapping(path = "/private")
    public ResponseEntity<Channel> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        return ResponseEntity.ok(channelService.create(request));
    }

    @PatchMapping(path = "/{channelId}")
    public ResponseEntity<Channel> update(
            @PathVariable("channelId") UUID id,
            @RequestBody ChannelUpdateRequest request) {
        return ResponseEntity.ok(channelService.update(id, request));
    }

    @DeleteMapping(path = "/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}