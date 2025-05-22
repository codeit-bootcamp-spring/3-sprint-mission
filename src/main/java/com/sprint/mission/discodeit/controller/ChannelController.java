package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@ResponseBody
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

  private final ChannelService channelService;

  /* 공개 채널 생성 */
  @Operation(summary = "Public Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Channel.class)))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = PublicChannelCreateRequest.class))
  )
  @PostMapping(path = "public")
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody PublicChannelCreateRequest publicChannelCreateRequest
  ) {
    Channel createdChannel = channelService.create(publicChannelCreateRequest);
    return ResponseEntity.created(URI.create(createdChannel.getId().toString()))
        .body(createdChannel);
  }

  /* 비공개 채널 생성 */
  @Operation(summary = "Private Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Channel.class)))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = PrivateChannelCreateRequest.class))
  )
  @PostMapping(path = "private")
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest
  ) {
    Channel createdChannel = channelService.create(privateChannelCreateRequest);
    return ResponseEntity.created(URI.create(createdChannel.getId().toString()))
        .body(createdChannel);
  }

  /* 공개 채널 수정 */
  @Operation(summary = "Channel 정보 수정")
  @Parameters({
      @Parameter(
          name = "channelId",
          description = "수정할 Channel ID",
          required = true,
          in = ParameterIn.PATH,
          schema = @Schema(type = "string", format = "uuid")
      )
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(examples = {
          @ExampleObject(value = "Channel with id {channelId} not found")
      })),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음", content = @Content(examples = {
          @ExampleObject(value = "Private channel cannot be updated")
      })),
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = Channel.class)))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = PublicChannelUpdateRequest.class))
  )
  @PatchMapping(path = "{channelId}")
  public ResponseEntity<Channel> update(
      @PathVariable String channelId,
      @RequestBody PublicChannelUpdateRequest publicChannelCreateRequest

  ) {
    Channel updatedChannel = channelService.update(parseStringToUuid(channelId),
        publicChannelCreateRequest);
    return ResponseEntity.ok().body(updatedChannel);
  }

  /* 채널 삭제 */
  @Operation(summary = "Channel 삭제")
  @Parameters({
      @Parameter(
          name = "channelId",
          description = "삭제할 Channel ID",
          required = true,
          in = ParameterIn.PATH,
          schema = @Schema(type = "string", format = "uuid")
      )
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(examples = {
          @ExampleObject(value = "Channel with id {channelId} not found")
      })),
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨")
  })
  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(
      @PathVariable String channelId
  ) {
    channelService.delete(parseStringToUuid(channelId));
    //TODO : 204번으로 리턴
    return ResponseEntity.ok().build();
  }

  /* 특정 사용자가 볼 수 있는 모든 채널 목록 조회 */
  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @Parameters({
      @Parameter(
          name = "userId",
          description = "조회할 User ID",
          required = true,
          in = ParameterIn.QUERY,
          schema = @Schema(type = "string", format = "uuid")
      )
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))))
  })
  @GetMapping()
  public ResponseEntity<List<ChannelDto>> findAllByUserId(
      @RequestParam String userId
  ) {
    List<ChannelDto> ChannelDtoList = channelService.findAllByUserId(parseStringToUuid(userId));
    return ResponseEntity.ok().body(ChannelDtoList);
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* String 타입 -> UUID 타입으로 변경 */
  private UUID parseStringToUuid(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("올바른 파라미터 형식이 아닙니다.");
    }
  }

}
