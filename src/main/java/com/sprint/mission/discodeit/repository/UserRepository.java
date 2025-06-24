package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    //  @EntityGraph(attributePaths = {"userStatus", "profile"})
    @Query("SELECT u FROM User u JOIN FETCH u.userStatus JOIN FETCH u.profile")
    @Override
    List<User> findAll();

    @Query("SELECT u FROM User u JOIN FETCH u.userStatus JOIN FETCH u.profile WHERE u.id = :uuid")
    @Override
    Optional<User> findById(UUID uuid);

    public Optional<User> findByUsername(String username);

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);
}
