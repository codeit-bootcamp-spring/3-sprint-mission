package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpaUserStatusRepository
 * Author       : dounguk
 * Date         : 2025. 5. 28.
 */
public interface JpaUserStatusRepository extends JpaRepository<UserStatus, UUID> {
}
