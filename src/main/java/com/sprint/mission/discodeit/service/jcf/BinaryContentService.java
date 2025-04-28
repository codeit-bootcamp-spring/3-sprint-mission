package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : BinaryContentService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
public interface BinaryContentService {

    //    create
//[ ] DTO를 활용해 파라미터를 그룹화합니다.
    BinaryContent create(BinaryContentCreateRequest request);

    //    find
//[ ] id로 조회합니다.
    BinaryContent find(UUID attachmentId);

    //    findAllByIdIn
//[ ] id 목록으로 조회합니다.
    List<BinaryContent> findAllByIdIn(List<UUID> attachments);


    //    delete
//[ ] id로 삭제합니다.
    void delete(UUID attachmentId);
}
