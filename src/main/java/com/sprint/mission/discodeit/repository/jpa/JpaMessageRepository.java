package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpqMessageRepository
 * Author       : dounguk
 * Date         : 2025. 5. 28.
 */
public interface JpaMessageRepository extends JpaRepository<Message, UUID> {

}
