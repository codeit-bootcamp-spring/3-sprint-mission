package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository
 * fileName       : BinaryContentRepository
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */
public interface BinaryContentRepository {
    BinaryContent createBinaryContent(byte[] image);

    //    BinaryContent findBinaryContentByUserId(UUID userId);
    BinaryContent updateImage(UUID profileId, byte[] image);

}
