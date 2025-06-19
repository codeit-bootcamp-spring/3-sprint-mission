package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotBlank;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user fileName       :
 * PublicChannelCreateRequest author         : doungukkim date           : 2025. 4. 25. description
 *   : =========================================================== DATE              AUTHOR
 *    NOTE ----------------------------------------------------------- 2025. 4. 25.
 * doungukkim       최초 생성
 */

public record PublicChannelCreateRequest(
    @NotBlank String name,
    String description) {}
