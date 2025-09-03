package com.sprint.mission.discodeit.auth.jwt.store;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, String> {

    List<JwtTokenEntity> findByUsername(String username);
}


