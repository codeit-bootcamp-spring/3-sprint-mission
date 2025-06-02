package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.dto.message
 * FileName     : JpaPageResponse
 * Author       : dounguk
 * Date         : 2025. 6. 1.
 */
@Builder
public record JpaMessagePageResponse (
        UUID id,
        User author,
        Channel channel,
        List<BinaryContent> attachments,
        String content
){
}
