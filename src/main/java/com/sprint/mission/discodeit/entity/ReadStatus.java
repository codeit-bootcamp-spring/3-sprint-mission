package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * packageName    : com.sprint.mission.discodeit.entity
 * fileName       : ReadStatus
 * author         : doungukkim
 * date           : 2025. 4. 23.
 * description    :
 */
// 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
@Entity
@Table(name = "read_statuses", schema = "discodeit")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadStatus extends BaseUpdatableEntity {

    @Column(name = "last_read_at")
    private Instant lastReadAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", unique = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(nullable = false)
    private boolean notificationEnabled;



    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
        this.notificationEnabled = channel.getType().equals(ChannelType.PRIVATE);
    }

    public void changeLastReadAt(Instant newLastReadAt, Boolean notificationEnabled) {
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
        }
        if (notificationEnabled != null) {
            this.notificationEnabled = notificationEnabled;
        }
    }

//    public void changeLastReadAt(Instant lastReadAt) {
//        this.lastReadAt = lastReadAt;
//    }
}
