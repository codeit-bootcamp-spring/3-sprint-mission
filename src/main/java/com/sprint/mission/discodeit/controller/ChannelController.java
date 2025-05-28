package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CodeMessageResponseDto;
import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Tag(
        name = "Channel",
        description = "Channel API"
)
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @Operation(
          summary = "Public Channel 생성",
          operationId = "create_3"
  )
  @ApiResponse(
          responseCode = "201",
          description = "Public Channel이 성공적으로 생성됨",
          content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = Channel.class)
          )
  )
  @PostMapping("/public")
  public ResponseEntity<?> create(@RequestBody PublicChannelCreateRequest request) {

      try {
          Channel createdChannel = channelService.create(request);
          return ResponseEntity
                  .status(HttpStatus.CREATED)
                  .body(createdChannel);
      } catch (RuntimeException e) {
          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.INTERNAL_ERROR,
                          ResponseMessage.INTERNAL_SERVER_ERROR
                  ));
      }
  }

  @Operation(
          summary = "Private Channel 생성",
          operationId = "create_4"
  )
  @ApiResponse(
          responseCode = "201",
          description = "Private Channel이 성공적으로 생성됨",
          content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = Channel.class)
          )
  )
  @PostMapping("/private")
  public ResponseEntity<?> create(@RequestBody PrivateChannelCreateRequest request) {
      try {
          Channel createdChannel = channelService.create(request);
          return ResponseEntity
                  .status(HttpStatus.CREATED)
                  .body(createdChannel);
      } catch (RuntimeException e) {
          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.INTERNAL_ERROR,
                          ResponseMessage.INTERNAL_SERVER_ERROR
                  ));
      }
  }

  @Operation(
          summary = "Public Channel 수정",
          description = "Public Channel의 정보를 수정합니다",
          tags = {"Channel"}
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Channel 수정 성공",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Success")
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Channel 수정 실패",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Fail")
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Channel을 찾을 수 없거나 관리자가 아님",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Channel not found or not an admin")
                  )
          )
  })
  @Parameter(
          required = true,
          content = @Content(
                  schema = @Schema(implementation = PublicChannelUpdateRequest.class)
          )
  )
  @Parameter(
          name = "channelId",
          description = "수정할 채널 ID",
          required = true,
          schema = @Schema(type = "string", format = "uuid")
  )
  @PutMapping("/{channelId}")
  public ResponseEntity<?> update(@PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
      try {
          Channel updatedChannel = channelService.update(channelId, request);
          return ResponseEntity
                  .status(HttpStatus.OK)
                  .body(updatedChannel);
      } catch (NoSuchElementException e) {
          return ResponseEntity
                  .status(HttpStatus.NOT_FOUND)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.CHANNEL_NOT_FOUND,
                          ResponseMessage.CHANNEL_NOT_FOUND
                  ));
      } catch (RuntimeException e) {
          return ResponseEntity
                  .status(HttpStatus.BAD_REQUEST)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.REQUEST_FAIL,
                          ResponseMessage.REQUEST_FAIL
                  ));
      }
  }

  @Operation(
          summary = "Channel 삭제",
          operationId = "delete_2"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "204",
                  description = "Channel이 성공적으로 삭제됨"
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Channel을 찾을 수 없음",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Channel with id {channelId} not found")
                  )
          )
  })
  @Parameter(
          name = "channelId",
          description = "삭제할 Channel ID",
          required = true,
          schema = @Schema(type = "string", format = "uuid")
  )
  @DeleteMapping("/{channelId}")
  public ResponseEntity<?> delete(@PathVariable("channelId") UUID channelId) {
      try {
          channelService.delete(channelId);
          return ResponseEntity
                  .status(HttpStatus.NO_CONTENT)
                  .body("Channel이 성공적으로 삭제됨");
      } catch (NoSuchElementException e) {
          return ResponseEntity
                  .status(HttpStatus.NOT_FOUND)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.CHANNEL_NOT_FOUND,
                          ResponseMessage.CHANNEL_NOT_FOUND
                  ));
      } catch (RuntimeException e) {
          return ResponseEntity
                  .status(HttpStatus.BAD_REQUEST)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.REQUEST_FAIL,
                          ResponseMessage.REQUEST_FAIL
                  ));
      }

  }

  @Operation(
          summary = "User가 참여 중인 Channel 목록 조회",
          operationId = "findAll_1"
  )
  @ApiResponse(
          responseCode = "200",
          description = "Channel 목록 조회 성공",
          content = @Content(
                  mediaType = "*/*",
                  array = @ArraySchema(
                          schema = @Schema(implementation = ChannelDto.class)
                  )
          )
  )
  @Parameter(
          name = "userId",
          description = "조회할 User ID",
          required = true,
          in = ParameterIn.QUERY,
          schema = @Schema(type = "string", format = "uuid")
  )
  @GetMapping("")
  public ResponseEntity<?> findAll(@RequestParam("userId") UUID userId) {
      try {
          List<ChannelDto> channels = channelService.findAllByUserId(userId);
          return ResponseEntity
                  .status(HttpStatus.OK)
                  .body(channels);
      } catch (RuntimeException e) {
          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.INTERNAL_ERROR,
                          ResponseMessage.INTERNAL_SERVER_ERROR
                  ));
      }
  }
}
