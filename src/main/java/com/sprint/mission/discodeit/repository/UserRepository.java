package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  /**
   * 사용자 객체의 고유 아이디로 조회
   *
   * @param id UUID
   * @return Optional<User>
   */
  Optional<User> findById(UUID id);


  /**
   * 사용자 이메일로 조회
   *
   * @param email 사용자 이메일
   * @return Optional<User>
   */
  Optional<User> findByEmail(String email);

  /**
   * 사용자 이름으로 조회
   *
   * @param name 사용자 이름
   * @return Optional<User>
   */
  Optional<User> findByName(String name);

  /**
   * 사용자 이름과 비밀번호로 조회 (Auth)
   *
   * @param name     사용자 이름
   * @param password 사용자 비밀번호
   * @return Optional<User>
   */
  Optional<User> findByNameWithPassword(String name, String password);

  /**
   * 모든 사용자 조회
   *
   * @return List<User>
   */
  List<User> findAll();

  /**
   * 사용자 저장
   *
   * @param user User
   * @return User
   */
  User save(User user);

  /**
   * 사용자 객체의 고유 아이디로 삭제
   *
   * @param id UUID
   */
  void deleteById(UUID id);
}