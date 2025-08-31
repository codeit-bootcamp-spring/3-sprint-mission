package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "read_statuses",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_read_statuses_user_channel",
        columnNames = {"user_id", "channel_id"}
    )
)
@Getter
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_read_statuses_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "channel_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_read_statuses_channel")
    )
    private Channel channel;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    @Column(name = "notification_enabled")
    private boolean notificationEnabled;

    public ReadStatus(User user, Channel channel, Instant lastReadAt, boolean notificationEnabled) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
        this.notificationEnabled = notificationEnabled;
    }

    public void update(Instant newLastReadAt, boolean newNotificationEnabled) {
        if (newLastReadAt != null) {
            this.lastReadAt = newLastReadAt;
        }

        this.notificationEnabled = newNotificationEnabled;
    }
}
