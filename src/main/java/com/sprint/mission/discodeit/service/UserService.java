package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  /**
   * 새로운 사용자를 생성한다
   *
   * @param email    사용자 email
   * @param name     사용자 이름
   * @param password 사용자 비밀번호
   * @return 생성된 사용자 객체
   */
  User create(String email, String name, String password);

  /**
   * 새로운 사용자를 생성한다
   *
   * @param dto UserCreateRequest
   * @return 생성된 사용자 객체
   */
  UserResponse create(UserCreateRequest dto);

  /**
   * ID로 사용자를 조회한다
   *
   * @param id 사용자 ID
   * @return Optional<UserResponse>
   */
  Optional<UserResponse> findById(UUID id);

  /**
   * 이름으로 사용자를 검색한다
   *
   * @param name 검색할 사용자 이름
   * @return Optional<User>
   */
  Optional<UserResponse> findByName(String name);

  /**
   * 이메일로 사용자를 검색한다
   *
   * @param email 검색할 사용자 이메일
   * @return Optional<UserResponse>
   */
  Optional<UserResponse> findByEmail(String email);

  /**
   * 모든 사용자를 조회한다
   *
   * @return 전체 사용자 목록
   */
  List<UserResponse> findAll();

  /**
   * 사용자 정보를 업데이트한다
   *
   * @param dto UserUpdateRequest
   * @return 업데이트된 사용자 객체
   */
  Optional<UserResponse> update(UserUpdateRequest dto);

  /**
   * 사용자를 삭제한다
   *
   * @param id 삭제할 사용자 ID
   * @return 삭제된 사용자 객체
   */
  Optional<UserResponse> delete(UUID id);
}