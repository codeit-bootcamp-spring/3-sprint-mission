package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "user_status")
@Table(name = "tbl_user_statuses")
@NoArgsConstructor
@Getter
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne // 양방향
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "last_active_at")
    private Instant lastActiveAt;

    public UserStatus(User user, Instant lastActiveAt) {
        super.setId(UUID.randomUUID());
        super.setCreatedAt(Instant.now());
        //
        this.user = user;
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
}
