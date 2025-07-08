package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channels")
@RestController
@Slf4j
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    // PUBLIC Channel Create( POST )
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> create(
        @RequestBody PublicChannelCreateRequest request
    ) {
        log.info("Public 채널 생성 요청 : {}", request);
        ChannelDto createdChannel = channelService.create(request);
        log.debug("Public 채널 생성 응답 : {}", createdChannel);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createdChannel);

    }


    // PRIVATE Channel Create( POST )
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> create(
        @RequestBody PrivateChannelCreateRequest request
    ) {
        log.info("Private 채널 생성 요청 : {}", request);
        ChannelDto createdChannel = channelService.create(request);
        log.debug("Private 채널 생성 응답 : {}", createdChannel);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createdChannel);
    }


    // 공개 채널 수정( PATCH )
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(
        @PathVariable UUID channelId
        , @RequestBody PublicChannelUpdateRequest request
    ) {
        log.info("Public 채널 수정 요청 : id = {}, request = {}", channelId, request);
        ChannelDto updatedChannel = channelService.update(channelId, request);

        // 유효성 검사
        if (updatedChannel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.debug("Public 채널 수정 응답 : {}", updatedChannel);

        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }


    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID channelId
    ) {
        log.info("채널 삭제 요청 : id = {}", channelId);
        channelService.delete(channelId);
        log.debug("채널 삭제 완료");
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    // 특정 사용자가 조회 가능한 모든 채널 목록 조회( GET )
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(
        @RequestParam("userId") UUID userId
    ) {
        log.info("사용자별 채널 목록 조회 요청 : userId = {}", userId);
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        log.debug("사용자별 채널 목록 조회 응답 : count = {}", channels.size());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channels);
    }
}
