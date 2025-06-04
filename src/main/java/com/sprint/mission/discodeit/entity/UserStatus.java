package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    private static final int TIMEOUT_MINUTES = 5;

    protected UserStatus() {
    }

    @Builder
    public UserStatus(User user, Instant lastActiveAt) {
        this.user = user;
        this.lastActiveAt = lastActiveAt != null ? lastActiveAt : Instant.now();
    }

    public void update(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt != null ? lastActiveAt : Instant.now();
    }

    public boolean isOnline() {

        Instant instantFiveMinuteAgo = Instant.now().minus(Duration.ofMinutes(TIMEOUT_MINUTES));

        return this.lastActiveAt.isAfter(instantFiveMinuteAgo);
    }
}
