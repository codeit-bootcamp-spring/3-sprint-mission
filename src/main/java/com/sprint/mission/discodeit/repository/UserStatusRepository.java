package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

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
   * 사용자 상태 저장
   *
   * @param userStatus UserStatus
   * @return UserStatus
   */
  UserStatus save(UserStatus userStatus);

  /**
   * 사용자 상태의 고유 아이디로 삭제
   *
   * @param id UUID
   */
  void delete(UUID id);
}
