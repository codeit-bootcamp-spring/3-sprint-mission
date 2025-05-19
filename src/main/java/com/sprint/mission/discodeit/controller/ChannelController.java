package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Channel", description = "Channel API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Public Channel 생성")
  @ApiResponse(
      responseCode = "201",
      description = "Public Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = ChannelResponse.class))
  )
  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> create(
      @RequestBody PublicChannelCreateRequest request) {
    ChannelResponse response = channelService.create(request);
    return ResponseEntity.created(URI.create("/api/channels/" + response.id()))
        .body(response);
  }

  @Operation(summary = "Private Channel 생성")
  @ApiResponse(
      responseCode = "201",
      description = "Private Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = ChannelResponse.class))
  )
  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> create(
      @RequestBody PrivateChannelCreateRequest request) {
    ChannelResponse response = channelService.create(request);
    return ResponseEntity.created(URI.create("/api/channels/" + response.id()))
        .body(response);
  }

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "Channel 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelResponse.class)))
  )
  @GetMapping
  public ResponseEntity<List<ChannelResponse>> findAllByUser(@Parameter UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ChannelResponse.class))
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Private Channel은 수정할 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
  })
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Optional<ChannelResponse> updated = channelService.update(channelId, request);
    return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "채널 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "Channel 정보가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음")
      }
  )
  @DeleteMapping("/{channelId}")
  public ResponseEntity<?> delete(@PathVariable UUID channelId) {
    Optional<ChannelResponse> deleted = channelService.delete(channelId);
    return deleted.map(r -> ResponseEntity.noContent().build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
