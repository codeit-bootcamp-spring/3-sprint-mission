package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;

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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping(path = "public")
    public ResponseEntity<ChannelDto> create(@RequestBody PublicChannelCreateRequest request) {
        log.info("POST /api/channels/public  name={}", request.name());
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdChannel);
    }

    @PostMapping(path = "private")
    public ResponseEntity<ChannelDto> create(@RequestBody PrivateChannelCreateRequest request) {
        log.info("POST /api/channels/private participants={}", request.participantIds().size());
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdChannel);
    }

    @PatchMapping(path = "{channelId}")
    public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID channelId,
                                             @RequestBody PublicChannelUpdateRequest request) {
        log.info("PATCH /api/channels/{}  newName={}", channelId, request.newName());
        ChannelDto updatedChannel = channelService.update(channelId, request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedChannel);
    }

    @DeleteMapping(path = "{channelId}")
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        log.info("DELETE /api/channels/{}", channelId);
        channelService.delete(channelId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        log.info("GET   /api/channels?userId={}", userId);
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channels);
    }
}
