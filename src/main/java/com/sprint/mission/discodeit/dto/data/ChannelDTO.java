package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelDTO {
    private ChannelType type;
    private String name;
    private UUID id;
    private Instant lastMessageAt;
    private Set<UUID> membersIds;

    @Override
    public String toString() {
        return "[ChannelDTO] {" + type + " name=" + name + " id=" +  id + ", lastMessageAt=" + lastMessageAt + ", membersIds=" + membersIds + "}";
    }
}
