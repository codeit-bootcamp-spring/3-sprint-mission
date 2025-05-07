package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/*사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다.
사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.*/
@Getter
public class ReadStatus {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel) {
        this.id = UUID.randomUUID();
        this.userId = user.getId();
        this.channelId = channel.getId();
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = this.createdAt;
        this.lastReadAt = this.updatedAt;
    }

    //TODO lastReadAt에 메시지가 생성된 시점 대입
    public void update(User user, Channel channel, Instant lastReadAt) {
        this.userId = user.getId();
        this.channelId = channel.getId();
        this.lastReadAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    // 채널에 읽지 않은 메시지가 있는지
    //TODO 채널이 업데이트 된 시간이 lastReadAt보다 이후인지
    public boolean hasUnreadMessages(Channel channel) { // true면 ReadStatus는 안읽음, false면 읽음
        return channel.getUpdatedAt().isAfter(lastReadAt);
    }
}
