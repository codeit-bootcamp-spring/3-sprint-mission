package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  /**
   * 새로운 사용자를 생성한다
   *
   * @param email    사용자 이메일
   * @param name     사용자 이름
   * @param password 사용자 비밀번호
   * @return 생성된 사용자 객체
   */
  User createUser(String email, String name, String password);

  /**
   * ID로 사용자를 조회한다
   *
   * @param id 사용자 ID
   * @return 조회된 사용자 객체
   */
  Optional<User> getUserById(UUID id);

  /**
   * 이름으로 사용자를 검색한다
   *
   * @param name 검색할 사용자 이름
   * @return 검색된 사용자 목록
   */
  List<User> searchUsersByName(String name);

  /**
   * 이메일로 사용자를 검색한다
   *
   * @param email 검색할 사용자 이메일
   * @return 검색된 사용자 객체
   */
  Optional<User> getUserByEmail(String email);

  /**
   * 모든 사용자를 조회한다
   *
   * @return 전체 사용자 목록
   */
  List<User> getAllUsers();

  /**
   * 사용자 정보를 업데이트한다
   *
   * @param id       사용자 ID
   * @param name     변경할 이름 (null인 경우 변경하지 않음)
   * @param password 변경할 비밀번호 (null인 경우 변경하지 않음)
   * @return 업데이트된 사용자 객체
   */
  Optional<User> updateUser(UUID id, String name, String password);

  /**
   * 사용자를 삭제한다
   *
   * @param id 삭제할 사용자 ID
   * @return 삭제된 사용자 객체
   */
  Optional<User> deleteUser(UUID id);
}