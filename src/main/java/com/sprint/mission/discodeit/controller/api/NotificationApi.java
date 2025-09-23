package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
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
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notification", description = "알림 조회/확인(삭제) API")
public interface NotificationApi {

    @Operation(summary = "내 알림 목록 조회", description = "접속한 사용자의 알림을 최신순으로 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "알림 목록 조회 성공",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)),
                examples = {
                    @ExampleObject(name = "예시",
                        value = """
                            [
                              {
                                "id": "8f9f5a36-5c69-4b30-8c3d-9e7f0c3a6fd0",
                                "createdAt": "2025-08-22T12:34:56Z",
                                "receiverId": "11111111-1111-1111-1111-111111111111",
                                "title": "보낸 사람 (#general)",
                                "content": "메시지 내용 미리보기"
                              }
                            ]
                            """)
                }
            )
        ),
        @ApiResponse(
            responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    ResponseEntity<List<NotificationDto>> getMyNotifications(
        @Parameter(hidden = true) DiscodeitUserDetails user
    );

    @Operation(summary = "내 알림 확인(삭제)", description = "알림을 확인 처리(삭제)합니다. 본인 알림만 삭제할 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(
            responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "403", description = "인가 실패(다른 사용자의 알림)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404", description = "알림이 존재하지 않음",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    ResponseEntity<Void> deleteMyNotification(
        @Parameter(name = "notificationId", description = "삭제할 알림 ID", in = ParameterIn.PATH)
        UUID notificationId,
        @Parameter(hidden = true) DiscodeitUserDetails user
    );
}
