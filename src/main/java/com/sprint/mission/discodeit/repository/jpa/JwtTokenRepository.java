package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.JwtTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JwtTokenRepository
 * Author       : dounguk
 * Date         : 2025. 8. 14.
 */
@Repository
public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, String> {
    List<JwtTokenEntity> findByUsername(String username);
}
