package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  /* 공개 채널 생성 */
  @PostMapping(path = "/public")
  @Override
  public ResponseEntity<ChannelDto> create(
      @RequestBody PublicChannelCreateRequest publicChannelCreateRequest
  ) {
    ChannelDto createdChannelDto = channelService.create(publicChannelCreateRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannelDto);
  }

  /* 비공개 채널 생성 */
  @PostMapping(path = "/private")
  @Override
  public ResponseEntity<ChannelDto> create(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest
  ) {
    ChannelDto createdChannelDto = channelService.create(privateChannelCreateRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannelDto);
  }

  /* 공개 채널 수정 */
  @PatchMapping(path = "/{channelId}")
  @Override
  public ResponseEntity<ChannelDto> update(
      @PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest publicChannelCreateRequest

  ) {
    ChannelDto updatedChannelDto = channelService.update(channelId,
        publicChannelCreateRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedChannelDto);
  }

  /* 채널 삭제 */
  @DeleteMapping(path = "/{channelId}")
  @Override
  public ResponseEntity<Void> delete(
      @PathVariable("channelId") UUID channelId
  ) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  /* 특정 사용자가 볼 수 있는 모든 채널 목록 조회 */
  @GetMapping
  @Override
  public ResponseEntity<List<ChannelDto>> findAllByUserId(
      @RequestParam("userId") UUID userId
  ) {
    List<ChannelDto> ChannelDtoList = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ChannelDtoList);
  }

}
