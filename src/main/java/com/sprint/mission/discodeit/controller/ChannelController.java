package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
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

    // 공개 채널 생성
    @PostMapping(path = "public")
    public ResponseEntity<ChannelDto> create(
            @Valid @RequestBody PublicChannelCreateRequest request
    ){
        ChannelDto channel = channelService.createPublicChannel(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channel);
    }

    // 비공개 채널 생성
    @PostMapping(path = "private")
    public ResponseEntity<ChannelDto> create(
            @Valid @RequestBody PrivateChannelCreateRequest request
    ){
        ChannelDto channel = channelService.createPrivateChannel(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channel);
    }

    // 공개 채널 이름 수정
    @PatchMapping(path = "{channelId}")
    public ResponseEntity<ChannelDto> update(
            @PathVariable("channelId") UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request
    ) {
        ChannelDto result = channelService.update(channelId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    // 채널 삭제
    @DeleteMapping(path = "{channelId}")
    public ResponseEntity<Void> delete(
            @PathVariable("channelId") UUID channelId
    ){
        channelService.delete(channelId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // 특정 사용자가 조회할 수 있는 채널
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(
            @RequestParam("userId") UUID userId
    ){
        List<ChannelDto> channels = channelService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }
}
