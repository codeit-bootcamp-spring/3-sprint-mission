package com.sprint.mission.discodeit.Dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;

import java.time.Instant;
import java.util.HashSet;
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
    List<User> users;

    public PublicChannelCreateRequest(Set<User> users, String channelName, String description) {
        this.users = users.stream().toList();
        this.channelName = channelName;
        this.description = description;
    }
}
