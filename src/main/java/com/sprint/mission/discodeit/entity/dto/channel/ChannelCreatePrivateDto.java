package com.sprint.mission.discodeit.entity.dto.channel;

import com.sprint.mission.discodeit.entity.common.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelCreatePrivateDto extends Period {

    private Set<UUID> users;
    private UUID adminId;

    public ChannelCreatePrivateDto(UUID adminId) {
        this.adminId = adminId;
        this.users = new HashSet<>();
    }
}
