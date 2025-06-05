package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.serviceDto.ChannelDto;
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
@RequestMapping("/api/channels")
@RestController
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping(path = "/public")
    public ResponseEntity<Channel> create(
        @Valid @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {

        Channel publicChannel = channelService.create(publicChannelCreateRequest);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(publicChannel);
    }

    @PostMapping(path = "/private")
    public ResponseEntity<Channel> create(
        @Valid @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {

        Channel privateChannel = channelService.create(privateChannelCreateRequest);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(privateChannel);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<Channel> update(
        @PathVariable UUID channelId,
        @Valid @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest
    ) {
        Channel updatedPublic = channelService.update(channelId, publicChannelUpdateRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedPublic);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
        @RequestParam("userId") UUID userId) {
        List<ChannelDto> channelList = channelService.findAllByUserId(userId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channelList);
    }
}
