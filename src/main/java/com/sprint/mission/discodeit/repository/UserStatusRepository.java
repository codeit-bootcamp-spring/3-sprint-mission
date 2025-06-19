package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  @EntityGraph(attributePaths = {"user"})
  public Optional<UserStatus> findByUserId(UUID userId);

  public void deleteByUserId(UUID userId);

//    /* CrudRepository의 기본 메소드 */
//    public UserStatus save(UserStatus userStatus);
//
//    /* CrudRepository의 기본 메소드 */
//    public Optional<UserStatus> findById(UUID userStatusId);
//
//    /* JpaRepository의 기본 메소드 */
//    public List<UserStatus> findAll();
//
//    /* CrudRepository의 기본 메소드 */
//    public boolean existsById(UUID userStatusId);
//
//    /* CrudRepository의 기본 메소드 */
//    public void deleteById(UUID userStatusId);
}
