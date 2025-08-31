package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  @EntityGraph(attributePaths = {"profile"})
  List<User> findAll();

  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findByUsername(String username);

  @Query("select u.id from User u where u.role = :role")
  List<UUID> findAllIdsByRole(@Param("role") Role role);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
