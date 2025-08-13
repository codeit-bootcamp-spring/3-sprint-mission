package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.mapper.ResponseMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
@Slf4j
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;
  private final MapperFacade mapperFacade;

  @PostMapping(path = "public")
  @Transactional
  public ResponseEntity<ChannelDto> create(@Valid @RequestBody PublicChannelCreateRequest request) {
    log.info("공개 채널 생성 API 요청 - 이름: {}, 설명: {}", request.name(), request.description());

    Channel createdChannel = channelService.create(request);
    ChannelDto response = mapperFacade.toDto(createdChannel);

    log.info("공개 채널 생성 API 응답 - 채널 ID: {}, 이름: {}", response.id(), response.name());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @PostMapping(path = "private")
  @Transactional
  public ResponseEntity<ChannelDto> create(@Valid @RequestBody PrivateChannelCreateRequest request) {
    log.info("비공개 채널 생성 API 요청 - 참가자 수: {}", request.participantIds().size());

    Channel createdChannel = channelService.create(request);
    ChannelDto response = mapperFacade.toDto(createdChannel);

    log.info("비공개 채널 생성 API 응답 - 채널 ID: {}, 참가자 수: {}",
        response.id(), response.participants().size());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @PatchMapping(path = "{channelId}")
  public ResponseEntity<ChannelResponse> update(
      @PathVariable("channelId") UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request) {
    log.info("채널 수정 API 요청 - 채널 ID: {}, 새 이름: {}", channelId, request.newName());

    Channel updatedChannel = channelService.update(channelId, request);
    ChannelResponse response = ResponseMapper.toResponse(updatedChannel);

    log.info("채널 수정 API 응답 - 채널 ID: {}, 수정된 이름: {}",
        response.id(), response.name());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    log.info("채널 삭제 API 요청 - 채널 ID: {}", channelId);

    channelService.delete(channelId);

    log.info("채널 삭제 API 완료 - 채널 ID: {}", channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    log.debug("사용자 채널 목록 API 요청 - 사용자 ID: {}", userId);

    List<ChannelDto> channels = channelService.findAllByUserId(userId);

    log.debug("사용자 채널 목록 API 응답 - 사용자 ID: {}, 채널 수: {}", userId, channels.size());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
