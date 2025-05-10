package com.sprint.mission.discodeit.Dto.channel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user
 * fileName       : PublicChannelCreateRequest
 * author         : doungukkim
 * date           : 2025. 4. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 25.        doungukkim       최초 생성
 */

public record PublicChannelCreateRequest(
        String channelName,
        String description,
        Set<String> userIds) {

    public PublicChannelCreateRequest {
        Objects.requireNonNull(channelName, "no channelName in request");
        Objects.requireNonNull(description, "no description in request");
        if ((userIds == null) || (userIds.isEmpty())) {
            throw new RuntimeException("request must need one or more ids");
        }
    }
}
