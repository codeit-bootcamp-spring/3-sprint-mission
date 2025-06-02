package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateDto;
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
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping(path = "/public")
    public ResponseEntity<ChannelResponseDto> createPublicChannel(
            @RequestBody PublicChannelDto publicChannelDTO) {
        ChannelResponseDto createdChannel = channelService.createPublicChannel(publicChannelDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @PostMapping(path = "/private")
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(
            @RequestBody PrivateChannelDto privateChannelDTO) {
        ChannelResponseDto createdChannel = channelService.createPrivateChannel(privateChannelDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponseDto>> findAllByUserId(@RequestParam UUID userId) {
        List<ChannelResponseDto> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }

    @PatchMapping(path = "/{channelId}")
    public ResponseEntity<ChannelResponseDto> update(@PathVariable UUID channelId,
                                                     @RequestBody PublicChannelUpdateDto publicChannelUpdateDTO) {
        ChannelResponseDto updatedChannel = channelService.update(channelId, publicChannelUpdateDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }

    @DeleteMapping(path = "/{channelId}")
    public ResponseEntity<String> deleteById(@PathVariable UUID channelId) {
        channelService.deleteById(channelId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
