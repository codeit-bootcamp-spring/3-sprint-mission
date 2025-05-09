package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

public interface UserService {

    // Create: 새로운 사용자 생성
    User createUser(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest);

    // Read: 사용자 ID로 단일 사용자 조회
    UserDto getUserById(UUID id);

    // 모든 사용자 조회
    List<UserDto> getAllUsers();

    // Update: 사용자 정보 업데이트(이름, 이메일, 비밀번호 포함됨)
    User updateUser(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest);

    // Delete: 특정 사용자 삭제
    void deleteUser(UUID id);
}
