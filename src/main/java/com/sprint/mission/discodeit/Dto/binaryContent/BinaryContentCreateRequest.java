package com.sprint.mission.discodeit.Dto.binaryContent;

import lombok.Getter;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.binaryContent
 * fileName       : BinaryContentCreatRequest
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public record BinaryContentCreateRequest(byte[] attachment) {
}


