package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channels")
@RestController
@Tag(
        name = "Channel"
        , description = "Channel API"
)
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채팅방 개설
    @Operation(
            summary = "Public Channel 생성"
            , operationId = "create_3"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Channel.class)))
            }
    )
    @PostMapping(
            value = "/public"
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Channel> createPublicChannel(
            @RequestBody PublicChannelCreateRequest publicChannelCreateDTO
    ) {

        Channel createdChannel = channelService.create(publicChannelCreateDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    // 비공개 채팅방 개설
    @Operation(
            summary = "Private Channel 생성"
            , operationId = "create_4"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Channel.class)))
            }
    )
    @Parameter(
            name = "PrivateChannelCreateRequest"
            , content = @Content(
            schema = @Schema(implementation = PrivateChannelCreateRequest.class)
    )
    )
    @PostMapping(
            value = "/private"
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateDTO
    ) {

        Channel createdChannel = channelService.create(privateChannelCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    // 공개 채널 정보 수정
    @Operation(
            summary = "Channel 정보 수정"
            , operationId = "update_3"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Channel 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = Channel.class)))
                    , @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음", content = @Content(schema = @Schema(example = "Private channel cannot be updated")))
                    , @ApiResponse(responseCode = "404", description = "Channel를 찾을 수 없음", content = @Content(schema = @Schema(example = "Channel with id {channelId} not found")))
            }
    )
    @Parameter(
            name = "channelId"
            , in = ParameterIn.PATH
            , description = "수정할 Channel ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @PatchMapping(
            value = "/{channelId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Channel> update(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelUpdateDTO
    ) {
        Channel channel = channelService.update(channelId, publicChannelUpdateDTO);
        return ResponseEntity.ok(channel);
    }

    // 채팅방 삭제
    @Operation(
            summary = "Channel 삭제"
            , operationId = "delete_2"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Channel가 성공적으로 삭제됨", content = @Content(schema = @Schema(hidden = true)))
                    , @ApiResponse(responseCode = "404", description = "Channel를 찾을 수 없음", content = @Content(schema = @Schema(example = "Channel with id {channelId} not found")))
            }
    )
    @Parameter(
            name = "channelId"
            , in = ParameterIn.PATH
            , description = "삭제할 Channel ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @DeleteMapping(
            value = "/{channelId}"
    )
    public ResponseEntity<String> delete(
            @PathVariable UUID channelId
    ) {
        ChannelDTO channel = channelService.find(channelId);
        String channelName = channel.name();

        channelService.delete(channelId);

        return ResponseEntity.ok(channelName + "채팅방을 삭제했습니다.");
    }

    // 특정 유저가 볼 수 있는 채널 목록 조회
    @Operation(
            summary = "User가 참여 중인 Channel 목록 조회"
            , operationId = "findAll_1"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공"
                            , content = @Content(array = @ArraySchema(schema = @Schema(type = "array", implementation = ChannelDTO.class))))
            }
    )
    @Parameter(
            name = "userId"
            , in = ParameterIn.QUERY
            , description = "조회할 User ID"
            , required = true
            , schema = @Schema(type = "string", format = "uuid")
    )
    @GetMapping()
    public ResponseEntity<List<ChannelDTO>> findByUserId(
            @RequestParam("userId") UUID userId
    ) {
        List<ChannelDTO> channels = channelService.findAllByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }

}
