package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpaUserRepository
 * Author       : dounguk
 * Date         : 2025. 5. 27.
 */
public interface JpaUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

//    @Query("SELECT m from User m LEFT JOIN FETCH m.profile LEFT JOIN FETCH m.status")
    @Query(" SELECT u FROM User u LEFT JOIN UserStatus s ON s.user = u LEFT JOIN FETCH u.profile")
    List<User> findAllWithBinaryContentAndUserStatus();
}
