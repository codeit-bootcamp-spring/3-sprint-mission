package com.sprint.mission.discodeit.Dto.binaryContent;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.binaryContent
 * fileName       : BinaryContentFindRequest
 * author         : doungukkim
 * date           : 2025. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 11.        doungukkim       최초 생성
 */
public record BinaryContentFindRequest(List<UUID> attachments) {

}
