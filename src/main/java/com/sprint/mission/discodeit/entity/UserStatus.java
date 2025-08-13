package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "tbl_user_statuses")
@NoArgsConstructor
@Getter
@DynamicUpdate
public class UserStatus extends BaseUpdatableEntity {

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY, optional = false) // 양방향
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user, Instant lastActiveAt) {
        setUser(user);
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }

    public Boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }

    public void setUser(User user) {
        this.user = user;
        user.setStatus(this);
    }
}
