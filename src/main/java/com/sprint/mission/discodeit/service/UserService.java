package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {

  /**
   * 새로운 사용자를 생성한다
   *
   * @param dto     UserCreateRequest
   * @param profile BinaryContent
   * @return 생성된 사용자 객체
   */
  UserResponse create(UserCreateRequest dto, BinaryContentCreateRequest profile);

  /**
   * ID로 사용자를 조회한다
   *
   * @param userId 사용자 ID
   * @return UserResponse
   */
  UserResponse findById(UUID userId);

  /**
   * 이름으로 사용자를 검색한다
   *
   * @param name 검색할 사용자 이름
   * @return UserResponse
   */
  UserResponse findByName(String name);

  /**
   * 이메일로 사용자를 검색한다
   *
   * @param email 검색할 사용자 이메일
   * @return UserResponse
   */
  UserResponse findByEmail(String email);

  /**
   * 모든 사용자를 조회한다
   *
   * @return 전체 사용자 목록
   */
  List<UserResponse> findAll();

  /**
   * 사용자 정보를 업데이트한다
   *
   * @param userId UUID
   * @param dto    UserUpdateRequest
   * @return 업데이트된 사용자 객체
   */
  UserResponse update(UUID userId, UserUpdateRequest dto,
      BinaryContentCreateRequest profile);

  /**
   * 사용자를 삭제한다
   *
   * @param userId 삭제할 사용자 ID
   * @return 삭제된 사용자 객체
   */
  UserResponse delete(UUID userId);
}