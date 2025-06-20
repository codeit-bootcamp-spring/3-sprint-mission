package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile LEFT JOIN FETCH u.status")
  List<User> findAllWithProfileAndStatus();

  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findByUsername(String username);

  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findByEmail(String userEmail);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByUsernameOrEmail(String username, String userEmail);

//  /* CrudRepository의 기본 메소드 */
//  public User save(User user);
//
//  /* CrudRepository의 기본 메소드 */
//  public Optional<User> findById(UUID userId);
//
//  /* JpaRepository의 기본 메소드 */
//  public List<User> findAll();
//
//  /* CrudRepository의 기본 메소드 */
//  public boolean existsById(UUID id);
//
//  /* CrudRepository의 기본 메소드 */
//  public void deleteById(UUID userId);
}
