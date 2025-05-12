package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.Channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channel")
@Controller
public class ChannelController {

    private final ChannelService channelService;
    private final UserService userService;

    /**
     * 새로운 공개 채널 생성
     *
     * @param publicChannelCreateRequestDTO 공개 채널 생성 요청 DTO
     * @return 생성된 Channel (HTTP 201 CREATED)
     */
    @RequestMapping(
            path = "/create/public",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> createPublicChannel(
            @RequestBody PublicChannelCreateRequestDTO publicChannelCreateRequestDTO
    ) {
        // 채널 생성
        Channel createdChannel = channelService.create(publicChannelCreateRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    /**
     * 새로운 비공개 채널 생성
     * 요청에 포함된 참여자 ID 목록의 유효성 검증 후, 채널 생성
     *
     * @param privateChannelCreateRequestDTO 비공개 채널 생성 요청 DTO
     * @return 생성된 Channel (HTTP 201 CREATED)
     */
    @RequestMapping(
            path = "/create/private",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequestDTO privateChannelCreateRequestDTO
    ) {
        // 유저 유효성 검증
        privateChannelCreateRequestDTO.participantIds().forEach(userService::find);

        // 채널 생성
        Channel createdChannel = channelService.create(privateChannelCreateRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    /**
     * 공개 채널 정보 수정
     *
     * @param channelId 수정할 채널의 ID
     * @param publicChannelUpdateRequestDTO 공개 채널 수정 요청 DTO
     * @return 생성된 Channel (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/update",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> updatePublicChannel(
            @RequestParam UUID channelId,
            @RequestBody PublicChannelUpdateRequestDTO publicChannelUpdateRequestDTO
    ) {
        // 채널 정보 수정
        Channel updatedChannel = channelService.update(channelId, publicChannelUpdateRequestDTO);

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
    @RequestMapping(
            path = "/delete",
            method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<String> delete(
            @RequestParam UUID channelId
    ) {
        // 채널 삭제
        channelService.delete(channelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("채널이 삭제되었습니다.");
    }

    /**
     * 특정 사용자가 볼 수 있는 모든 채널 목록 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 볼 수 있는 Channel 목록 (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/findAll",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<ChannelDTO>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        // 유저 유효성 검증
        userService.find(userId);

        // 유저가 볼 수 있는 모든 채널 조회
        List<ChannelDTO> channels = channelService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }
}
