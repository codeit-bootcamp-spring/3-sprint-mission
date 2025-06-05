package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  @EntityGraph(attributePaths = {"profile", "userStatus"})
  Optional<User> findByUsername(String username);

  @EntityGraph(attributePaths = {"profile", "userStatus"})
  Optional<User> findByEmail(String email);

  void deleteById(@NonNull UUID id);
}
