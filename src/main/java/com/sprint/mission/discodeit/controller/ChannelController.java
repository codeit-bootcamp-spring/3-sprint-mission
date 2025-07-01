package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Channel", description = "Channel API")
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@RestController
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "Public Channel 생성")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
        content = @Content(mediaType = "*/*", schema = @Schema(implementation = Channel.class)))
    @PostMapping(path = "/public")
    public ResponseEntity<Channel> createPublic(
        @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {

        Channel publicChannel = channelService.create(publicChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
    }

    @Operation(summary = "Private Channel 생성")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
        content = @Content(mediaType = "*/*", schema = @Schema(implementation = Channel.class)))
    @PostMapping(path = "/private")
    public ResponseEntity<Channel> createPrivate(
        @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {

        Channel privateChannel = channelService.create(privateChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
    }

    @Operation(summary = "Channel 정보 수정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
            content = @Content(schema = @Schema(implementation = Channel.class))),
        @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
            content = @Content(mediaType = "*/*",
                examples = @ExampleObject(value = "Private channel cannot be updated"))),
        @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
            content = @Content(mediaType = "*/*",
                examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    @PatchMapping("/{channelId}")
    public ResponseEntity<Channel> updatePublic(
        @Parameter(description = "수정할 Channel ID") @PathVariable UUID channelId,
        @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest
    ) {
        Channel updatedPublic = channelService.update(channelId, publicChannelUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedPublic);
    }

    @Operation(summary = "Channel 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
        @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
            content = @Content(mediaType = "*/*",
                examples = @ExampleObject(value = "Channel with id {channelId} not found")))
    })
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "삭제할 Channel ID", required = true) @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
        content = @Content(mediaType = "*/*"))
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
        @Parameter(description = "조회할 User ID", required = true) UUID userId
    ) {
        List<ChannelDto> channelList = channelService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(channelList);
    }
}
