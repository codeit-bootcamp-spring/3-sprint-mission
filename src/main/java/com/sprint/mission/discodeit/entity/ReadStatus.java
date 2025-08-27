package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;

/**
 * 사용자가 특정 채널에서 메시지를 읽은 상태를 관리하는 엔티티 클래스입니다.
 * 
 * <p>사용자별로 채널의 읽음 상태와 알림 설정을 추적하며, 마지막 읽은 시간을
 * 기록하여 새로운 메시지 여부를 판단하는 데 사용됩니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>사용자와 채널 간의 읽음 상태 매핑</li>
 *   <li>마지막 읽은 시간 추적</li>
 *   <li>알림 활성화/비활성화 설정</li>
 *   <li>동적 업데이트 및 수정 시간 자동 기록</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Entity
@Table(name = "tbl_read_statuses")
@Getter
@NoArgsConstructor
@DynamicUpdate
public class ReadStatus extends BaseUpdatableEntity {

    /**
     * 읽음 상태를 관리하는 사용자입니다.
     * 
     * <p>다대일 관계로 연결되며, 지연 로딩을 통해 성능을 최적화합니다.</p>
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 읽음 상태가 관리되는 채널입니다.
     * 
     * <p>다대일 관계로 연결되며, 지연 로딩을 통해 성능을 최적화합니다.</p>
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    /**
     * 해당 채널에 대한 알림 활성화 여부입니다.
     * 
     * <p>기본값은 false이며, 사용자가 설정을 변경할 수 있습니다.</p>
     */
    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled = false;

    /**
     * 사용자가 해당 채널에서 마지막으로 메시지를 읽은 시간입니다.
     * 
     * <p>새로운 메시지 여부를 판단하는 기준이 되며, 안전한 timestamp 범위를 검증합니다.</p>
     */
    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    /**
     * 새로운 읽음 상태를 생성합니다.
     * 
     * @param user 읽음 상태를 관리할 사용자
     * @param channel 읽음 상태가 관리될 채널
     * @param notificationEnabled 알림 활성화 여부
     * @param lastReadAt 마지막 읽은 시간 (null이거나 잘못된 값인 경우 현재 시간으로 설정)
     */
    public ReadStatus(User user, Channel channel, boolean notificationEnabled, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.notificationEnabled = notificationEnabled;

        // 안전한 timestamp 설정
        if (lastReadAt == null || lastReadAt.getEpochSecond() < 0 || lastReadAt.getEpochSecond() > 253402300799L) {
            this.lastReadAt = Instant.now();
        } else {
            this.lastReadAt = lastReadAt;
        }
    }

    /**
     * 읽음 상태 정보를 업데이트합니다.
     * 
     * <p>변경된 값이 있는 경우에만 수정 시간을 갱신합니다.</p>
     * 
     * @param newLastReadAt 새로운 마지막 읽은 시간 (변경하지 않으려면 null)
     * @param newNotificationEnabled 새로운 알림 활성화 여부
     */
    public void update(Instant newLastReadAt, boolean newNotificationEnabled) {
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (newNotificationEnabled != this.notificationEnabled) {
            this.notificationEnabled = newNotificationEnabled;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }
}
