package com.sprint.mission.discodeit.Dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel
 * fileName       : FindChannelRequest
 * author         : doungukkim
 * date           : 2025. 4. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 26.        doungukkim       최초 생성
 */
@Getter
public class ChannelFindResponse {

    private Channel channel;
    private Instant recentMessageTime;
    private ChannelType type;
    private List<UUID> userIds;

    public ChannelFindResponse(Channel channel, Instant recentMessageTime) {
        this.channel = channel;
        this.recentMessageTime = recentMessageTime;
        this.type = ChannelType.PUBLIC;
    }

    public ChannelFindResponse(Channel channel, Instant recentMessageTime, List<UUID> userIds) {

        this.channel = channel;
        this.recentMessageTime = recentMessageTime;
        this.userIds = userIds;
        this.type = ChannelType.PRIVATE;
    }
}
