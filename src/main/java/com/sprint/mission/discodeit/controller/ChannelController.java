package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channels")
@RestController
public class ChannelController {

  private final ChannelService channelService;

  // PUBLIC Channel Create( POST )
  @Operation(summary = "공개 채널 생성", description = "공개 채널을 생성합니다")
  @ApiResponse(responseCode = "200", description = "공개 채널 생성 성공")
  @PostMapping("/public")
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody PublicChannelCreateRequest request
  ) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(createdChannel);

  }


  // PRIVATE Channel Create( POST )
  @Operation(summary = "비공개 채널 생성", description = "비공개 채널을 생성합니다")
  @ApiResponse(responseCode = "200", description = "비공개 채널 생성 성공")
  @PostMapping("/private")
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest request
  ) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(createdChannel);
  }


  // 공개 채널 수정( PATCH )
  @Operation(summary = "공개 채널 정보 수정", description = "지정된 공개 채널의 정보를 수정합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "공개 채널 수정 성공"),
      @ApiResponse(responseCode = "404", description = "해당 채널을 찾을 수 없습니다")
  })
  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> updatePublicChannel(
      @Parameter(description = "채널 ID", required = true, example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
      @PathVariable UUID channelId
      , @RequestBody PublicChannelUpdateRequest request
  ) {
    Channel updatedChannel = channelService.update(channelId, request);

    // 유효성 검사
    if (updatedChannel == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }


  // 채널 삭제( DEL )
  @Operation(summary = "채널 삭제", description = "지정된 채널을 삭제합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "채널 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "해당 채널을 찾을 수 없습니다")
  })
  @DeleteMapping("/{channelId}")
  public ResponseEntity<String> delete(
      @Parameter(description = "채널 ID", required = true)
      @PathVariable UUID channelId
  ) {
    try {
      channelService.delete(channelId);
      return ResponseEntity.status(HttpStatus.OK).body("채널 삭제에 성공하였습니다");
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("해당 채널을 찾을 수 없습니다");
    }
  }

  // 특정 사용자가 조회 가능한 모든 채널 목록 조회( GET )
  @Operation(summary = "사용자 채널 목록 조회", description = "사용자가 속한 모든 채널 목록을 조회합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "채널 목록 조회 성공"),
      @ApiResponse(responseCode = "204", description = "조회 가능한 채널이 없습니다", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUserId(
      @Parameter(description = "사용자 ID", required = true)
      @RequestParam("userId") UUID userId
  ) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
