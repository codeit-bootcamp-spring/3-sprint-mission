package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
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
    BinaryContent updateImage(UUID profileId, byte[] image); // both : throw

    void deleteBinaryContentById(UUID attachmentId); // file, jcf : throw exception

    BinaryContent findById(UUID attachmentId);

    List<BinaryContent> findAllByIds(List<UUID> attachmentIds);
}
