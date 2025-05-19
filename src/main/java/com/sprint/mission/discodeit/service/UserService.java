package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // 생성
    User create(UserCreateRequest userCreateRequest,
            BinaryContentCreateRequest profileCreateRequestDTO);

    // 전체 조회
    List<UserDto> findAll();

    // 조회 (ID, DTO응답)
    UserDto find(UUID userId);

    // 수정
    User update(UUID userId, UserUpdateRequest updateRequestDTO,
            BinaryContentCreateRequest profileCreateRequestDTO);

    // 삭제
    void delete(UUID userId);
}
