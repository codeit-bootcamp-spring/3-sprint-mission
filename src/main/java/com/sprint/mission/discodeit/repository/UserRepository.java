package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

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
