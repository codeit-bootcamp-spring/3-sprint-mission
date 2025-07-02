package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Table(
        name = "read_statuses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "channel_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", columnDefinition = "uuid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", columnDefinition = "uuid", nullable = false)
    private Channel channel;

    @Column(name = "last_read_at", columnDefinition = "timestamp with time zone", nullable = false)
    private Instant lastReadAt;

    @Builder
    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant newLastReadAt) {
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
        }
    }
}
