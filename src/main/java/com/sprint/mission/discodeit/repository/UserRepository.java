package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  @Query("SELECT u FROM User u JOIN FETCH u.status")
  @NonNull
  List<User> findAll();

  Optional<User> findByUsername(String username);

  Optional<User> findByUsernameAndPassword(String username, String password);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
