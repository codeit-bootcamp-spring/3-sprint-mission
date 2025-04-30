package com.sprint.mission.discodeit.Dto.channel;

import lombok.Getter;

import java.util.List;
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
 */@Getter
public class PublicChannelCreateRequest {
    String channelName;
    String description;
    List<UUID> userIds;

    public PublicChannelCreateRequest(Set<UUID> userIds, String channelName, String description) {
        this.userIds = userIds.stream().toList();
        this.channelName = channelName;
        this.description = description;
    }
}
