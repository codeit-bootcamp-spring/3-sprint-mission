package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = "profile")
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = "profile")
    List<User> findAll();

    boolean existsByRole(Role role);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    List<User> findByRole(Role role);

    @EntityGraph(attributePaths = "profile")
    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

}
