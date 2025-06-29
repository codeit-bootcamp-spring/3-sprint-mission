package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "read_statuses", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"}))
@Getter
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "last_read_at")
    private Instant lastReadAt;


    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
    }

    public void update(User user, Channel channel) {
        if (user != null && !user.equals(this.user)) {
            this.user = user;
        }
        if (channel != null && !channel.equals(this.channel)) {
            this.channel = channel;
        }
    }

    public void updateLastReadAt(Instant newLastReadAt) {
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return "ReadStatus{" +
                "lastReadAt=" + formatter.format(lastReadAt) +
                '}';
    }

}
