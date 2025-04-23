package com.sprint.mission.discodeit.repository;

import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository
 * fileName       : ReadStatusRepository
 * author         : doungukkim
 * date           : 2025. 4. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 23.        doungukkim       최초 생성
 */
public interface ReadStatusRepository {
    Instant readingTime(UUID readStatusId);


}
