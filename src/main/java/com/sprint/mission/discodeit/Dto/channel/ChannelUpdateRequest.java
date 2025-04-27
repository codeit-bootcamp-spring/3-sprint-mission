package com.sprint.mission.discodeit.Dto.channel;

import lombok.Getter;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel
 * fileName       : ChannelUpdateRequest
 * author         : doungukkim
 * date           : 2025. 4. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 27.        doungukkim       최초 생성
 */
@Getter
public class ChannelUpdateRequest {
    UUID channelId;
    String name;

    public ChannelUpdateRequest(UUID channel, String name) {
        this.channelId = channel;
        this.name = name;
    }
}
