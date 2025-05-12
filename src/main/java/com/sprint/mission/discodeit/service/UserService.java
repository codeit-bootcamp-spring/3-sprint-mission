package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.User.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.User.UserDTO;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 생성
    User create(UserCreateRequestDTO userCreateRequestDTO, BinaryContentCreateRequestDTO profileCreateRequestDTO);
    // 전체 조회
    List<UserDTO> findAll();
    // 조회 (ID, DTO응답)
    UserDTO find(UUID userId);
    // 수정
    User update(UUID userId, UserUpdateRequestDTO updateRequestDTO, BinaryContentCreateRequestDTO profileCreateRequestDTO);
    // 삭제
    void delete(UUID userId);
}
