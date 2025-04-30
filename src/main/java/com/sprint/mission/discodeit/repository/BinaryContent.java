package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository
 * fileName       : BinaryContent
 * author         : doungukkim
 * date           : 2025. 4. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 23.        doungukkim       최초 생성
 */
public interface BinaryContent {

    UUID saveBinaryContent(String image);

    List<UUID> findAllBinaryContents(List<UUID> binaryContentId);

    UUID finaBinaryContent(UUID binaryContentId);

    void removeBinaryContent(UUID binaryContentId);

}
