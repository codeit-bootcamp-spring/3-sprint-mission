package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "User 등록")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
          @ApiResponse(responseCode = "409", description = "같은 email 또는 name을 사용하는 User가 이미 존재함"
              , content = @Content(examples = {
              @ExampleObject(value = "User with email {email} already exists")}))
      }
  )
 ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserRequestDto userRequestDTO,
      @Parameter(description = "User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile profile);

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
  ResponseEntity<List<UserResponseDto>> findAll();

  @Operation(summary = "User 정보 수정")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
          @ApiResponse(responseCode = "400", description = "같은 email 또는 name을 사용하는 User가 이미 존재함"
              , content = @Content(examples = {
              @ExampleObject(value = "user with email {newEmail} already exists")})),
          @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "User with id {userId} not found")}))
      }
  )
  ResponseEntity<UserResponseDto> update(@PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateDto userUpdateDTO,
      @Parameter(description = "수정할 User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile);

  @Operation(summary = "User 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "User with id {userId} not found")}))
      }
  )
  ResponseEntity<String> deleteById(
      @Parameter(description = "삭제할 User ID") @PathVariable UUID userId);

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨"),
          @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "UserStatus with userId {userId} not found")}))
      }
  )
  ResponseEntity<UserStatusResponseDto> updateUserStatus(
      @Parameter(description = "상태를 변경할 User ID") @PathVariable UUID userId,
      @RequestBody UserStatusUpdateDto userStatusUpdateDTO);
}
