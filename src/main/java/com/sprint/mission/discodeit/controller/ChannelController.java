package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping("/public")
    @Override
    public ResponseEntity<ChannelResponse> create(
        @Valid @RequestBody PublicChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        ChannelResponse response = ChannelResponse.fromEntity(createdChannel);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }


    @PostMapping("/private")
    @Override
    public ResponseEntity<ChannelResponse> create(
        @Valid @RequestBody PrivateChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        ChannelResponse response = ChannelResponse.fromEntity(createdChannel);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @PatchMapping("/{channelId}")
    @Override
    public ResponseEntity<ChannelResponse> update(@PathVariable("channelId") UUID channelId,
        @Valid @RequestBody PublicChannelUpdateRequest request) {
        Channel updatedChannel = channelService.update(channelId, request);
        ChannelResponse response = ChannelResponse.fromEntity(updatedChannel);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @DeleteMapping("/{channelId}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channels);
    }
}