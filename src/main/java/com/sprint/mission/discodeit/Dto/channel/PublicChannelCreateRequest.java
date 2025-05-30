package com.sprint.mission.discodeit.Dto.channel;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user fileName       :
 * PublicChannelCreateRequest author         : doungukkim date           : 2025. 4. 25. description
 *   : =========================================================== DATE              AUTHOR
 *    NOTE ----------------------------------------------------------- 2025. 4. 25.
 * doungukkim       최초 생성
 */

public record PublicChannelCreateRequest(
    @NotNull String name,
    @NotNull String description) {}
