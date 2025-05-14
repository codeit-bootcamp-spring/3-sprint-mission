package com.sprint.mission.discodeit.entity.dto.channel;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.common.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class ChannelCreatePrivateDto extends Period {

    private Set<User> users;
    private User admin;

    public ChannelCreatePrivateDto(User admin) {
        this.admin = admin;
        this.users = new HashSet<>();
    }
}
