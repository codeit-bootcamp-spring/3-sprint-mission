package com.sprint.mission.discodeit.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> create(
      @RequestBody @Valid PublicChannelCreateRequest request) {
    ChannelResponse response = channelService.create(
        request.name(),
        request.description());
    return ResponseEntity.created(URI.create("/api/channels/" + response.id()))
        .body(response);
  }

  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> create(
      @RequestBody @Valid PrivateChannelCreateRequest request) {
    ChannelResponse response = channelService.create(
        request.participantIds());
    return ResponseEntity.created(URI.create("/api/channels/" + response.id()))
        .body(response);
  }

  @GetMapping
  public ResponseEntity<List<ChannelResponse>> findAllByUser(@Parameter UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> update(
      @PathVariable UUID channelId,
      @RequestBody @Valid PublicChannelUpdateRequest request) {
    return ResponseEntity.ok(channelService.update(
        channelId,
        request.newName(),
        request.newDescription()));
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }
}
