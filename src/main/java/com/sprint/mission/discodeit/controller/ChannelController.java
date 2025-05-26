package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
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
public class ChannelController {

  private final ChannelService channelService;


  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublicChannel(
      @RequestBody PublicChannelCreateRequest request) {
    System.out.println("📥 채널 생성 요청: " + request);
    System.out.println("➡ name: " + request.name());
    System.out.println("➡ description: " + request.description());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPublicChannel(request));
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest request) {
    System.out.println("📥 채널 생성 요청: " + request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPrivateChannel(request));
  }


  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    return ResponseEntity.ok(channelService.update(channelId, request));
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> delete(@PathVariable UUID channelId) {
    ChannelResponse deletedChannel = channelService.delete(channelId);
    return ResponseEntity.ok(deletedChannel);
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }


}
