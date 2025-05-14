package com.sprint.mission.discodeit.entity.dto.channel;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class ChannelUpdateRequestDto {
    private UUID channelId;
    private UUID adminId;
    private String channelName;
    private String channelDescription;

//    채널명, 설명 모두 바꿀 경우
    public ChannelUpdateRequestDto(UUID adminId, String channelDescription, UUID channelId, String channelName) {
        this.adminId = adminId;
        this.channelDescription = channelDescription;
        this.channelId = channelId;
        this.channelName = channelName;
    }
//    채널명만 바꿀 경우
    public ChannelUpdateRequestDto(UUID adminId, UUID channelId, String channelName) {
        this.adminId = adminId;
        this.channelId = channelId;
        this.channelName = channelName;
    }
//    채널 설명만 바꿀 경우
    public ChannelUpdateRequestDto(UUID adminId, String channelDescription, UUID channelId) {
        this.adminId = adminId;
        this.channelDescription = channelDescription;
        this.channelId = channelId;
    }
}
