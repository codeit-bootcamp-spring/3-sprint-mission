package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
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
  User createUser(String email, String name, String password);

  /**
   * 새로운 사용자를 생성한다
   *
   * @param ucr UserCreateRequest
   * @return 생성된 사용자 객체
   */
  UserResponse createUser(UserCreateRequest ucr);

  /**
   * ID로 사용자를 조회한다
   *
   * @param id 사용자 ID
   * @return Optional<UserResponse>
   */
  Optional<UserResponse> getUserById(UUID id);

  /**
   * 이름으로 사용자를 검색한다
   *
   * @param name 검색할 사용자 이름
   * @return Optional<User>
   */
  Optional<UserResponse> getUserByName(String name);

  /**
   * 이메일로 사용자를 검색한다
   *
   * @param email 검색할 사용자 이메일
   * @return Optional<UserResponse>
   */
  Optional<UserResponse> getUserByEmail(String email);

  /**
   * 모든 사용자를 조회한다
   *
   * @return 전체 사용자 목록
   */
  List<UserResponse> getAllUsers();

  /**
   * 사용자 정보를 업데이트한다
   *
   * @param urr UserUpdateRequest
   * @return 업데이트된 사용자 객체
   */
  Optional<UserResponse> updateUser(UserUpdateRequest urr);

  /**
   * 사용자를 삭제한다
   *
   * @param id 삭제할 사용자 ID
   * @return 삭제된 사용자 객체
   */
  Optional<UserResponse> deleteUser(UUID id);
}