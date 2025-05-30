package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpaBinaryContentRepository
 * Author       : dounguk
 * Date         : 2025. 5. 28.
 */
@Repository
public interface JpaBinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
    List<BinaryContent> findAllByIdIn(Collection<UUID> ids);

}
