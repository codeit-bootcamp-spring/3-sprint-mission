package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
@Slf4j
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping(path = "public")
    public ResponseEntity<ChannelResponse> create(
        @Valid @RequestBody PublicChannelCreateRequest request) {
        log.info("[ChannelController] Received request to create public channel. [name={}]",
            request.name());

        ChannelResponse createdChannel = channelService.createPublicChannel(request);

        log.debug("[ChannelController] Public channel created. [id={}]", createdChannel.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @PostMapping(path = "private")
    public ResponseEntity<ChannelResponse> create(
        @Valid @RequestBody PrivateChannelCreateRequest request) {
        log.info(
            "[ChannelController] Received request to create private channel. [participants={}]",
            request.participantIds());

        ChannelResponse createdChannel = channelService.createPrivateChannel(request);

        log.debug("[ChannelController] Private channel created. [id={}]", createdChannel.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> update(
        @PathVariable UUID channelId,
        @Valid @RequestBody PublicChannelUpdateRequest request) {
        log.info("[ChannelController] Received request to update channel. [id={}]", channelId);

        ChannelResponse updatedChannel = channelService.update(channelId, request);

        log.debug("[ChannelController] Channel updated. [id={}]", updatedChannel.id());
        return ResponseEntity.ok(updatedChannel);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> delete(@PathVariable UUID channelId) {
        log.info("[ChannelController] Received request to delete channel. [id={}]", channelId);

        try {
            ChannelResponse deletedChannel = channelService.delete(channelId);
            log.debug("[ChannelController] Channel deleted. [id={}]", deletedChannel.id());
            return ResponseEntity.ok(deletedChannel);
        } catch (Exception e) {
            log.error("[ChannelController] Error while deleting channel. [id={}]", channelId, e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> findAllByUserId(@RequestParam UUID userId) {
        log.info("[ChannelController] Received request to find channels by user. [userId={}]",
            userId);

        List<ChannelResponse> channels = channelService.findAllByUserId(userId);

        log.debug("[ChannelController] Channels found. [count={}] [userId={}]", channels.size(),
            userId);
        return ResponseEntity.ok(channels);
    }
}
