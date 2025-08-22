package com.sprint.mission.discodeit.security.jwt.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, String> {

    List<JwtTokenEntity> findByUsername(String username);
}
