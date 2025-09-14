package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.custom.CustomUserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, CustomUserRepository {

    // 중복 검사
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // 유저 조회 (이름)
    Optional<User> findByUsername(String userName);

    boolean existsByRole(Role role);

    List<User> findAllByRole(Role role);

}
