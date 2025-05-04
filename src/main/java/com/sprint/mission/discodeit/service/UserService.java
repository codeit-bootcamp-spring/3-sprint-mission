package com.sprint.mission.discodeit.service;

// 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
// 일관성 유지, 유지보수 용이, 확장성 제공

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserPasswordUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest);

    UserDTO find(UUID id);

    List<UserDTO> findAll();

    User update(UUID id, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest);

    // 비밀번호 업데이트 ( 보안상 기능 분리 )
    User updateByPass(UUID id, UserPasswordUpdateRequest userPasswordUpdateRequest);

    void delete(UUID id);

}
