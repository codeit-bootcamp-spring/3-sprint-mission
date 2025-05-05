package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

  /**
   * 사용자 상태 삽입 (이미 존재 시 예외)
   *
   * @param userStatus UserStatus
   */
  void insert(UserStatus userStatus);

  /**
   * 사용자 상태 객체의 고유 아이디로 조회
   *
   * @param id UUID
   * @return Optional<UserStatus>
   */
  Optional<UserStatus> findById(UUID id);

  /**
   * 사용자 객체의 고유 아이디로 조회
   *
   * @param userId UUID
   * @return Optional<UserStatus>
   */
  Optional<UserStatus> findByUserId(UUID userId);

  /**
   * 모든 사용자 상태 조회
   *
   * @return List<UserStatus>
   */
  List<UserStatus> findAll();

  /**
   * 사용자 상태 저장 또는 업데이트
   *
   * @param userStatus UserStatus
   * @return UserStatus
   */
  UserStatus save(UserStatus userStatus);

  /**
   * 사용자 상태 업데이트 (존재하지 않으면 예외)
   *
   * @param userStatus UserStatus
   */
  void update(UserStatus userStatus);

  /**
   * 사용자 상태의 고유 아이디로 삭제
   *
   * @param id UUID
   */
  void delete(UUID id);
}
