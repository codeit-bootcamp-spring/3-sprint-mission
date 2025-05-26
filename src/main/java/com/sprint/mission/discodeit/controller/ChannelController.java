package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final UserService userService;

    /**
     * 새로운 공개 채널 생성
     *
     * @param publicChannelCreateRequest 공개 채널 생성 요청 DTO
     * @return 생성된 Channel (HTTP 201 CREATED)
     */
    @PostMapping(path = "/public")
    @Override
    public ResponseEntity<Channel> create(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest
    ) {
        Channel createdChannel = channelService.create(publicChannelCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    /**
     * 새로운 비공개 채널 생성 요청에 포함된 참여자 ID 목록의 유효성 검증 후, 채널 생성
     *
     * @param privateChannelCreateRequest 비공개 채널 생성 요청 DTO
     * @return 생성된 Channel (HTTP 201 CREATED)
     */
    @PostMapping(path = "/private")
    @Override
    public ResponseEntity<Channel> create(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest
    ) {
        // 유저 유효성 검증
        privateChannelCreateRequest.participantIds().forEach(userService::find);

        Channel createdChannel = channelService.create(privateChannelCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    /**
     * 공개 채널 정보 수정
     *
     * @param channelId                  수정할 채널의 ID
     * @param publicChannelUpdateRequest 공개 채널 수정 요청 DTO
     * @return 생성된 Channel (HTTP 200 OK)
     */
    @PatchMapping(path = "/{channelId}")
    @Override
    public ResponseEntity<Channel> update(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest
    ) {
        Channel updatedChannel = channelService.update(channelId, publicChannelUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedChannel);
    }

    /**
     * 채널 삭제
     *
     * @param channelId 삭제할 채널의 ID
     * @return 삭제 완료 메시지 (HTTP 200 OK)
     */
    @DeleteMapping(path = "/{channelId}")
    @Override
    public ResponseEntity<Void> delete(
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * 특정 사용자가 볼 수 있는 모든 채널 목록 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 볼 수 있는 Channel 목록 (HTTP 200 OK)
     */
    @GetMapping
    @Override
    public ResponseEntity<List<ChannelDto>> findAll(
            @RequestParam("userId") UUID userId
    ) {
        // 유저 유효성 검증
        userService.find(userId);

        List<ChannelDto> channels = channelService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }
}
