package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import lombok.NoArgsConstructor;

// 사용자별 마지막 접속 시간( last+5 : 온라인 ) / 판별 메서드 정의
@Getter
@Entity
@NoArgsConstructor
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
        foreignKey = @ForeignKey(name = "fk_user_status_user", foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    // 생성자
    public UserStatus(User user, Instant lastActiveAt) {
        this.user = user;
        this.lastActiveAt = lastActiveAt;
    }

    // Update 메서드
    public void update(Instant lastOnlineAt) {
        if (lastOnlineAt != null && lastOnlineAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastOnlineAt;
        }
    }

    // 온라인 상태 판별 메서드 정의
    public boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
