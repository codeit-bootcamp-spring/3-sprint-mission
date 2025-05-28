package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpaUserRepository
 * Author       : dounguk
 * Date         : 2025. 5. 27.
 */
public interface JpaUserRepository extends JpaRepository<User, UUID> {

}
