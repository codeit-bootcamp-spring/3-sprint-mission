package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

    @Setter
    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "last_active_at", columnDefinition = "timestamp with time zone", nullable = false)
    private Instant lastActiveAt;

    private static final int TIMEOUT_MINUTES = 5;

    @Builder
    public UserStatus(User user, Instant lastActiveAt) {
        setUser(user);
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
        }
    }

    public boolean isOnline() {

        Instant instantFiveMinuteAgo = Instant.now().minus(Duration.ofMinutes(TIMEOUT_MINUTES));

        return this.lastActiveAt.isAfter(instantFiveMinuteAgo);
    }
}
