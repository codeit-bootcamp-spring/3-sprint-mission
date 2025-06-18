package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channels")
@RestController
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    // PUBLIC Channel Create( POST )
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> create(
        @RequestBody PublicChannelCreateRequest request
    ) {
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createdChannel);

    }


    // PRIVATE Channel Create( POST )
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> create(
        @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createdChannel);
    }


    // 공개 채널 수정( PATCH )
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(
        @PathVariable UUID channelId
        , @RequestBody PublicChannelUpdateRequest request
    ) {
        ChannelDto updatedChannel = channelService.update(channelId, request);

        // 유효성 검사
        if (updatedChannel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }


    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    // 특정 사용자가 조회 가능한 모든 채널 목록 조회( GET )
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(
        @RequestParam("userId") UUID userId
    ) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channels);
    }
}
