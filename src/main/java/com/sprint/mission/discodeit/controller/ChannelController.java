package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 채널 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 *
 * <p>공개/비공개 채널 생성, 수정, 삭제, 사용자 채널 목록 조회 기능을 제공합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@RestController
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    private static final String CONTROLLER_NAME = "[ChannelController] ";

    /**
     * 공개 채널을 생성합니다.
     *
     * @param publicChannelCreateRequest 공개 채널 생성 요청 정보
     * @return 생성된 채널
     */
    @PostMapping(path = "/public")
    public ResponseEntity<ChannelDto> create(
            @Valid @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {

        log.info(CONTROLLER_NAME + "공개 채널 생성 시도: {}", publicChannelCreateRequest);

        ChannelDto publicChannel = channelService.create(publicChannelCreateRequest);

        log.info(CONTROLLER_NAME + "공개 채널 생성 성공: {}", publicChannel);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(publicChannel);
    }

    /**
     * 비공개 채널을 생성합니다.
     *
     * @param privateChannelCreateRequest 비공개 채널 생성 요청 정보
     * @return 생성된 채널
     */
    @PostMapping(path = "/private")
    public ResponseEntity<ChannelDto> create(
            @Valid @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {

        log.info(CONTROLLER_NAME + "비공개 채널 생성 시도: {}", privateChannelCreateRequest);

        ChannelDto privateChannel = channelService.create(privateChannelCreateRequest);

        log.info(CONTROLLER_NAME + "비공개 채널 생성 성공: {}", privateChannel);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(privateChannel);
    }

    /**
     * 공개 채널 정보를 수정합니다.
     *
     * @param channelId 채널 ID
     * @param publicChannelUpdateRequest 수정 요청 정보
     * @return 수정된 채널
     */
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(
            @PathVariable UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest
    ) {
        log.info(CONTROLLER_NAME + "공개 채널 수정 시도: id={}, {}", channelId, publicChannelUpdateRequest);

        ChannelDto updatedPublic = channelService.update(channelId, publicChannelUpdateRequest);

        log.info(CONTROLLER_NAME + "공개 채널 수정 성공: {}", updatedPublic);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedPublic);
    }

    /**
     * 채널을 삭제합니다.
     *
     * @param channelId 삭제할 채널 ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        log.info(CONTROLLER_NAME + "채널 삭제 시도: id={}", channelId);

        channelService.delete(channelId);

        log.info(CONTROLLER_NAME + "채널 삭제 성공: id={}", channelId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * 사용자 ID로 채널 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 채널 DTO 목록
     */
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @RequestParam("userId") UUID userId) {

        log.info(CONTROLLER_NAME + "사용자 채널 목록 조회 시도: userId={}", userId);

        List<ChannelDto> channelList = channelService.findAllByUserId(userId);

        log.info(CONTROLLER_NAME + "사용자 채널 목록 조회 성공: 건수={}", channelList.size());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channelList);
    }
}

