package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelAPI;
import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
public class ChannelController implements ChannelAPI {

  private final ChannelService channelService;

  // 공개 채팅방 개설
  @PostMapping(
      value = "/public"
      , consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ChannelDTO> create(
      @RequestBody PublicChannelCreateRequest publicChannelCreateDTO
  ) {

    ChannelDTO createdChannel = channelService.create(publicChannelCreateDTO);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  // 비공개 채팅방 개설
  @PostMapping(
      value = "/private"
      , consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ChannelDTO> create(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateDTO
  ) {

    ChannelDTO createdChannel = channelService.create(privateChannelCreateDTO);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  // 공개 채널 정보 수정
  @PatchMapping(
      value = "/{channelId}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ChannelDTO> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest publicChannelUpdateDTO
  ) {
    ChannelDTO channel = channelService.update(channelId, publicChannelUpdateDTO);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channel);
  }

  // 채팅방 삭제
  @DeleteMapping(
      value = "/{channelId}"
  )
  public ResponseEntity<String> delete(
      @PathVariable UUID channelId
  ) {
    ChannelDTO channel = channelService.find(channelId);
    String channelName = channel.name();

    channelService.delete(channelId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(channelName + "채팅방을 삭제했습니다.");
  }

  // 특정 유저가 볼 수 있는 채널 목록 조회
  @GetMapping()
  public ResponseEntity<List<ChannelDTO>> findAll(
      @RequestParam("userId") UUID userId
  ) {
    List<ChannelDTO> channels = channelService.findAllByUserId(userId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }

}
