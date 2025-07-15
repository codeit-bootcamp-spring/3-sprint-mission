package com.sprint.mission.discodeit.dto.binaryContent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes) { }


