package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentWithBytes;
import com.sprint.mission.discodeit.Dto.binaryContent.FindBinaryContentResponse;
import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.http.ResponseEntity;

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

    JpaBinaryContentResponse find(UUID binaryContentId);

    List<JpaBinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds);
}
