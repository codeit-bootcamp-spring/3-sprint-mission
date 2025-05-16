package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DTO로 파라미터 받는 거 테스트용 서비스 인터페이스
 */

public interface UserService {

    //     유저 생성 (UserCreateRequest DTO 활용)
    User createUser(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileImage);

    // 기존 유저 리스트에 새로운 유저 추가
    void addUserToRepository(User user);

    // 유저 아이디 이용해서 조회
    UserDto getUserById(UUID id);

    // 유저 이름 이용해서 조회
    Optional<UserDto> searchUsersByName(String name);

    // 유저 전체 조회
    List<UserDto> getAllUsers();

    // 유저 이름 변경
    boolean updateUserName(UUID id, String newName);

    boolean updateProfileImage(UUID userId, Optional<BinaryContentCreateRequest> request);

    // id 이용해서 유저 삭제
    boolean deleteUserById(UUID id);

}