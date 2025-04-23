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
    // 호출시 uuid를 받아서 user, message에 보관
    UUID saveBinaryContent(String image);

    // list로 받아서 들오온 수 만큼 찾아서 return(단건, 다건)
    List<UUID> findAllBinaryContents(List<UUID> binaryContentId);

    //
    UUID finaBinaryContent(UUID binaryContentId);

    void removeBinaryContent(UUID binaryContentId);

}
