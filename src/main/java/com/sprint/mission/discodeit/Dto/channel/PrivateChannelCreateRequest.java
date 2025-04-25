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
 * fileName       : PrivateChannelCreateRequest
 * author         : doungukkim
 * date           : 2025. 4. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 25.        doungukkim       최초 생성
 */
@Getter
public class PrivateChannelCreateRequest {

    List<User> users;

    public PrivateChannelCreateRequest(Set<User>users) {
        HashSet<User> usersSet = new HashSet<>(users);
        this.users =  usersSet.stream().toList();
    }
}
