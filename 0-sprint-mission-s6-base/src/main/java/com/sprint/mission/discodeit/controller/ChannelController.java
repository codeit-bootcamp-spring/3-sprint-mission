package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.request.ChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.global.response.CustomApiResponse;
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

  @PostMapping(path = "public")
  @Override
  public ResponseEntity<ChannelResponse> createPublicChannel(
      @RequestBody ChannelRequest.CreatePublic request) {
    ChannelResponse createChannel = channelService.createPublicChannel(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createChannel);
  }

  @PostMapping(path = "/private")
  @Override
  public ResponseEntity<ChannelResponse> createPrivateChannel(
      @RequestBody ChannelRequest.CreatePrivate request) {
    ChannelResponse createChannel = channelService.createPrivateChannel(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createChannel);
  }

  @PatchMapping(path = "{channelId}")
  @Override
  public ResponseEntity<ChannelResponse> update(
      @PathVariable("channelId") UUID channelId,
      @RequestBody ChannelRequest.Update request) {
    ChannelResponse updateChannel = channelService.update(channelId, request);
    return ResponseEntity.ok(updateChannel);
  }

  @DeleteMapping(path = "{channelId}")
  @Override
  public ResponseEntity<Void> delete(
      @PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  @Override
  public ResponseEntity<List<ChannelResponse>> findAll(
      @RequestParam("userId") UUID userId) {
    List<ChannelResponse> channels = channelService.findAllByUserId(userId);
    if (channels == null) {
      channels = List.of();
    }
    return ResponseEntity.ok(channels);
  }
}
