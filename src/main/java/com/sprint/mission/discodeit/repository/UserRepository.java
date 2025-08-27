package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  List<User> findAllByRole(Role role);

  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findByUsername(String username);

  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findByEmail(String email);

  void deleteById(@NonNull UUID id);

  boolean existsByRole(Role role);
}
